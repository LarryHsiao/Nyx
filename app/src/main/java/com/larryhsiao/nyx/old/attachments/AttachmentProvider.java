package com.larryhsiao.nyx.old.attachments;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MatrixCursor.RowBuilder;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.attachments.*;
import com.larryhsiao.nyx.core.jots.*;
import com.larryhsiao.nyx.core.tags.TagDb;
import com.silverhetch.aura.uri.UriMimeType;
import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.database.SingleConn;
import com.larryhsiao.clotho.database.h2.EmbedH2Conn;
import com.larryhsiao.clotho.source.ConstSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static android.provider.DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL;
import static com.larryhsiao.nyx.NyxApplication.URI_FILE_PROVIDER;

/**
 * Document provider for exposing attachments.
 */
public class AttachmentProvider extends DocumentsProvider {
    private static final String ROOT_DOC_ID = "root:Jotted";
    private static final String ID_PREFIX_JOT = "jot:";
    private static final String ID_PREFIX_ATTACHMENT = "attachment:";
    private static final String[] DEFAULT_ROOT_PROJECTION = new String[]{
        Root.COLUMN_ROOT_ID,
        Root.COLUMN_MIME_TYPES,
        Root.COLUMN_FLAGS,
        Root.COLUMN_ICON,
        Root.COLUMN_TITLE,
        Root.COLUMN_SUMMARY,
        Root.COLUMN_DOCUMENT_ID,
        Root.COLUMN_AVAILABLE_BYTES,
        Root.COLUMN_CAPACITY_BYTES,
    };
    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[]{
        Document.COLUMN_DOCUMENT_ID,
        Document.COLUMN_MIME_TYPE,
        Document.COLUMN_DISPLAY_NAME,
        Document.COLUMN_LAST_MODIFIED,
        Document.COLUMN_FLAGS,
        Document.COLUMN_SIZE,
        Document.COLUMN_SUMMARY,
    };
    private File attachmentRoot;
    private File thumbnailRoot;
    private Source<Connection> db;

    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        final MatrixCursor result = new MatrixCursor(DEFAULT_ROOT_PROJECTION);
        final RowBuilder row = result.newRow();
        row.add(Root.COLUMN_ROOT_ID, "root");
        row.add(Root.COLUMN_SUMMARY, getContext().getString(R.string.Attachments_in_Jots));
        row.add(Root.COLUMN_FLAGS, Root.FLAG_SUPPORTS_RECENTS | Root.FLAG_SUPPORTS_SEARCH);
        row.add(Root.COLUMN_TITLE, getContext().getString(R.string.app_name));
        row.add(Root.COLUMN_DOCUMENT_ID, ROOT_DOC_ID);
        row.add(Root.COLUMN_MIME_TYPES, "image/*\nvideo/*");
        row.add(Root.COLUMN_AVAILABLE_BYTES, attachmentRoot.getFreeSpace());
        row.add(Root.COLUMN_CAPACITY_BYTES, attachmentRoot.getTotalSpace());
        row.add(Root.COLUMN_ICON, R.mipmap.ic_launcher);
        return result;
    }

    @Override
    public Cursor queryDocument(String id, String[] projection) throws FileNotFoundException {
        MatrixCursor res = new MatrixCursor(DEFAULT_DOCUMENT_PROJECTION);
        if (ROOT_DOC_ID.equals(id)) {
            final RowBuilder row = res.newRow();
            row.add(Document.COLUMN_DOCUMENT_ID, id);
            row.add(Document.COLUMN_SUMMARY, getContext().getString(R.string.Attachments_in_Jots));
            row.add(Document.COLUMN_DISPLAY_NAME, getContext().getString(R.string.app_name));
            row.add(Document.COLUMN_LAST_MODIFIED, attachmentRoot.lastModified());
            row.add(Document.COLUMN_FLAGS, FLAG_SUPPORTS_THUMBNAIL);
            row.add(Document.COLUMN_SIZE, new FileSize(attachmentRoot).value());
            row.add(Document.COLUMN_ICON, R.mipmap.ic_launcher);
            row.add(Document.COLUMN_MIME_TYPE, "vnd.android.document/directory");
        } else if (id.startsWith(ID_PREFIX_JOT)) {
            jotResult(res, Collections.singletonList(
                new JotById(Long.parseLong(id.replace(ID_PREFIX_JOT, "")), db).value()
            ));
        } else if (id.startsWith(ID_PREFIX_ATTACHMENT)) {
            attachmentsResult(res, Collections.singletonList(
                new AttachmentById(
                    db,
                    Long.parseLong(id.replace(ID_PREFIX_ATTACHMENT, ""))
                ).value()
            ));
        }
        return res;
    }

    @Override
    public Cursor queryChildDocuments(String parentId, String[] projection, String order)
        throws FileNotFoundException {
        final MatrixCursor res = new MatrixCursor(DEFAULT_DOCUMENT_PROJECTION);
        if (ROOT_DOC_ID.equals(parentId)) {
            jotResult(res, new QueriedJots(new AllJots(db)).value());
        }
        if (parentId.startsWith(ID_PREFIX_JOT)) {
            attachmentsResult(
                res,
                new QueriedAttachments(
                    new AttachmentsByJotId(
                        db,
                        Long.parseLong(parentId.replace(ID_PREFIX_JOT, ""))
                    )
                ).value().stream()
                    .filter(it -> it.uri().startsWith(URI_FILE_PROVIDER))
                    .collect(Collectors.toList())
            );
        }
        return res;
    }

    private void attachmentsResult(MatrixCursor res, List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            if (!new IsLocalExist(getContext(), attachment.uri()).value()) {
                continue;
            }
            File attachmentFile = fileByAttachment(attachment);
            final RowBuilder row = res.newRow();
            row.add(Document.COLUMN_DOCUMENT_ID, ID_PREFIX_ATTACHMENT + attachment.id());
            row.add(Document.COLUMN_SUMMARY, getContext().getString(R.string.Attachments_in_Jots));
            row.add(Document.COLUMN_DISPLAY_NAME, String.format("%06d", attachment.id()));
            row.add(Document.COLUMN_FLAGS, FLAG_SUPPORTS_THUMBNAIL);
            row.add(Document.COLUMN_ICON, R.mipmap.ic_launcher);
            row.add(
                Document.COLUMN_MIME_TYPE,
                new UriMimeType(getContext(), attachment.uri()).value()
            );
            row.add(Document.COLUMN_LAST_MODIFIED, attachmentFile.lastModified());
            row.add(Document.COLUMN_SIZE, attachmentFile.length());
        }
    }

    private void jotResult(MatrixCursor res, List<Jot> jots) {
        for (Jot jot : jots) {
            String content = jot.content().isEmpty() ?
                getContext().getString(R.string.Untitled) : jot.content();
            final RowBuilder row = res.newRow();
            row.add(Document.COLUMN_DOCUMENT_ID, ID_PREFIX_JOT + jot.id());
            row.add(Document.COLUMN_SUMMARY, content);
            row.add(Document.COLUMN_DISPLAY_NAME, content);
            row.add(Document.COLUMN_FLAGS, FLAG_SUPPORTS_THUMBNAIL);
            row.add(Document.COLUMN_MIME_TYPE, "vnd.android.document/directory");
            row.add(Document.COLUMN_LAST_MODIFIED, jot.createdTime());
        }
    }

    @Override
    public ParcelFileDescriptor openDocument(
        String id,
        String mode,
        @Nullable CancellationSignal signal
    ) throws FileNotFoundException {
        return ParcelFileDescriptor.open(fileByAttachmentId(id.replace(ID_PREFIX_ATTACHMENT, "")),
            ParcelFileDescriptor.parseMode(mode));
    }

    @Override
    public Cursor querySearchDocuments(String rootId, String query, String[] projection)
        throws FileNotFoundException {
        final MatrixCursor res = new MatrixCursor(DEFAULT_DOCUMENT_PROJECTION);
        jotResult(res, new QueriedJots(new JotsByKeyword(db, query)).value());
        return res;
    }

    private File fileByAttachmentId(String id) {
        return fileByAttachment(
            new AttachmentById(
                db,
                Long.parseLong(id)
            ).value()
        );
    }

    private File fileByAttachment(Attachment attachment) {
        return new AttachmentFileSource(getContext(), Uri.parse(attachment.uri())).value();
    }

    @Override
    public AssetFileDescriptor openDocumentThumbnail(
        String id,
        Point sizeHint,
        CancellationSignal signal
    ) throws FileNotFoundException {
        Attachment attachment =
            new AttachmentById(db, Long.parseLong(id.replace(ID_PREFIX_ATTACHMENT, ""))).value();
        File attachmentFile = fileByAttachment(attachment);
        File imageFile = attachmentFile;
        if (new UriMimeType(getContext(), attachment.uri()).value().startsWith("video")) {
            imageFile = new File(thumbnailRoot, attachment.id() + "");
            if (!imageFile.exists()) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(getContext(), Uri.fromFile(attachmentFile));
                mmr.getFrameAtTime().compress(JPEG, 50, new FileOutputStream(imageFile));
            }
        }
        return new AssetFileDescriptor(
            ParcelFileDescriptor.open(imageFile, MODE_READ_ONLY),
            0,
            AssetFileDescriptor.UNKNOWN_LENGTH
        );
    }

    @Nullable
    @Override
    public Cursor queryRecentDocuments(
        @NonNull String rootId,
        @Nullable String[] projection,
        @Nullable Bundle args,
        @Nullable CancellationSignal signal) throws FileNotFoundException {
        super.queryRecentDocuments(rootId, projection, args, signal);
        final MatrixCursor res = new MatrixCursor(DEFAULT_DOCUMENT_PROJECTION);
        if (ROOT_DOC_ID.equals(rootId)) {
            jotResult(
                res,
                new QueriedJots(new AllJots(db))
                    .value()
                    .stream()
                    .limit(10)
                    .collect(Collectors.toList())
            );
        }
        return res;
    }

    @Override
    public boolean onCreate() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        attachmentRoot = new File(getContext().getFilesDir(), "attachments");
        thumbnailRoot = getContext().getCacheDir();
        File dbFile = new File(getContext().getFilesDir(), "jot");
        db = new SingleConn(new AttachmentDb(
            new TagDb(
                new JotsDb(
                    new EmbedH2Conn(
                        new ConstSource<>(dbFile)
                    )
                )
            )
        ));
        return true;
    }
}
