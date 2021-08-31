package com.larryhsiao.nyx.core.attachments;

import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.util.List;

public class LocalAttachments implements Attachments {
    private final Source<Connection> db;

    public LocalAttachments(Source<Connection> db) {this.db = db;}

    @Override
    public List<Attachment> all() {
        return new QueriedAttachments(new AllAttachments(db)).value();
    }

    @Override
    public Attachment newAttachment(Attachment attachment) {
        return new NewAttachment(db, attachment.uri(), attachment.jotId()).value();
    }

    @Override
    public void newAttachmentWithId(Attachment attachment) {
        new NewAttachmentWithId(db, attachment).fire();
    }

    @Override
    public void replace(Attachment attachment) {
        new UpdateAttachment(db, attachment, false).fire();
    }

    @Override
    public Attachment byId(long id) {
        return new AttachmentById(db, id).value();
    }

    @Override
    public void update(Attachment attachment) {
        new UpdateAttachment(db, attachment).fire();
    }

    @Override
    public List<Attachment> byJotId(long id) {
        return new QueriedAttachments(new AttachmentsByJotId(db, id)).value();
    }

    @Override
    public void deleteById(long id) {
        new RemovalAttachment(db,id).fire();
    }
}
