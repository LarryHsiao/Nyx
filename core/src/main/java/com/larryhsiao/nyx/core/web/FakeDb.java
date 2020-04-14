package com.larryhsiao.nyx.core.web;

import com.larryhsiao.nyx.core.attachments.AttachmentDb;
import com.larryhsiao.nyx.core.jots.NewJot;
import com.silverhetch.clotho.Source;

import java.sql.Connection;

public class FakeDb implements Source<Connection> {
    private final Source<Connection> db;
    public FakeDb(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public Connection value() {
        Connection conn =db.value();
        new NewJot(db,""+System.currentTimeMillis()).value();
        new NewJot(db,""+System.currentTimeMillis()).value();
        new NewJot(db,""+System.currentTimeMillis()).value();
        new NewJot(db,""+System.currentTimeMillis()).value();
        return conn;
    }
}
