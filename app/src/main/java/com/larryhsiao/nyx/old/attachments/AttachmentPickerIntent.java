package com.larryhsiao.nyx.old.attachments;

import android.content.Intent;
import com.larryhsiao.clotho.Source;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;

/**
 * Source to build picker intent.
 */
public class AttachmentPickerIntent implements Source<Intent> {
    @Override
    public Intent value() {
        final Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return intent;
    }
}
