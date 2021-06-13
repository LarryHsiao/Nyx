package com.larryhsiao.nyx.attachment;

import android.content.Context;
import android.net.Uri;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.larryhsiao.nyx.NyxApplication;
import com.larryhsiao.nyx.R;
import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.file.SizeText;

/**
 * Action to show a attachment dialog for showing properties of attachment.
 */
public class AttachmentPropertiesDialog implements Action {
    private final Context context;
    private final Uri uri;

    public AttachmentPropertiesDialog(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    @Override
    public void fire() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setView(R.layout.dialog_properties)
            .show();
        TextView textView = dialog.findViewById(R.id.properties_text);
        textView.setText(context.getString(R.string.Uri___, uri.toString()));
        textView.append("\n");
        if (!uri.toString().startsWith(NyxApplication.URI_FILE_PROVIDER)){
            return;
        }
        textView.append(context.getString(
            R.string.Size___,
            new SizeText(
                new FileSize(new AttachmentFileSource(context, uri).value())
            ).value()
        ));
    }
}
