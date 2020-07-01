package com.larryhsiao.nyx.sync;

import android.content.Context;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larryhsiao.nyx.core.attachments.*;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Action to sync attachments.
 */
public class SyncAttachments implements Action {
    private final Context context;
    private final DocumentReference dataRef;
    private final Source<Connection> db;
    private final String uid;
    private final String key;

    public SyncAttachments(
        Context context,
        DocumentReference dataRef,
        Source<Connection> db,
        String uid,
        String key
    ) {
        this.context = context;
        this.dataRef = dataRef;
        this.db = db;
        this.uid = uid;
        this.key = key;
    }

    @Override
    public void fire() {
        try {
            CollectionReference remoteDb = dataRef.collection("attachments");
            Task<QuerySnapshot> task = remoteDb.get();
            QuerySnapshot result = Tasks.await(task);
            if (task.isSuccessful()) {
                sync(remoteDb, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sync(CollectionReference remoteDb, QuerySnapshot result) {
        Map<Long, Attachment> dbItems = new QueriedAttachments(
            new AllAttachments(db, true)
        ).value().stream().collect(Collectors.toMap(Attachment::id, attachment -> attachment));
        for (QueryDocumentSnapshot remoteItem : result) {
            final Attachment dbItem = dbItems.get(Long.valueOf(remoteItem.getId()));
            if (dbItem == null) {
                newLocalItem(remoteItem);
            } else {
                long remoteVersion = remoteItem.getLong("version");
                if (dbItem.version() < remoteVersion) {
                    updateLocalItem(remoteItem);
                    dbItems.remove(dbItem.id());
                } else if (dbItem.version() == remoteVersion) {
                    dbItems.remove(dbItem.id());
                }
            }
        }
        // new Items or local version is newer
        for (Attachment attachment : dbItems.values()) {
            updateRemoteItem(remoteDb, attachment);
        }
        new LocalFileSync(context, db, integer -> null).fire(); // for deleted items
        new RemoteFileSync(context, db, uid, key).fire();
    }

    private void updateLocalItem(QueryDocumentSnapshot remoteItem) {
        new UpdateAttachment(
            db,
            new ConstAttachment(
                Long.parseLong(remoteItem.getId()),
                remoteItem.getLong("jot_id"),
                remoteItem.getString("uri"),
                remoteItem.getLong("version").intValue(),
                remoteItem.getLong("delete").intValue()
            ), false
        ).fire();
    }

    private void newLocalItem(QueryDocumentSnapshot remoteItem) {
        new NewAttachmentById(
            db,
            new ConstAttachment(
                Long.parseLong(remoteItem.getId()),
                remoteItem.getLong("jot_id"),
                remoteItem.getString("uri"),
                remoteItem.getLong("version").intValue(),
                remoteItem.getLong("delete").intValue()
            )
        ).fire();
    }

    private void updateRemoteItem(CollectionReference remoteDb, Attachment attachment) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("delete", attachment.deleted() ? 1 : 0);
            data.put("version", attachment.version());
            data.put("jot_id", attachment.jotId());
            data.put("uri", attachment.uri());
            Tasks.await(remoteDb.document(attachment.id() + "").set(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
