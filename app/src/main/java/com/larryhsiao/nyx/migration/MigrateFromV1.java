package com.larryhsiao.nyx.migration;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.larryhsiao.nyx.jots.NewJot;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

/**
 * Action migrate database from v1
 */
public class MigrateFromV1 implements Action {
    private final Source<Connection> conn;
    private final Context context;

    public MigrateFromV1(Source<Connection> conn, Context context) {
        this.conn = conn;
        this.context = context;
    }

    @Override
    public void fire() {
        final String dbPath = context.getDatabasePath("database_nyx").getAbsolutePath();
        if (!new File(dbPath).exists()) {
            // Migration not required.
            return;
        }
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(
            dbPath,
            null, SQLiteDatabase.OPEN_READWRITE
        );
        try (Cursor cursor = db.rawQuery("SELECT * FROM diary;", null)) {
            Calendar calendar = Calendar.getInstance();
            while (cursor.moveToNext()) {
                Date date = new Date(cursor.getLong(cursor.getColumnIndex("timestamp")));
                calendar.setTime(date);
                new NewJot(
                    conn,
                    cursor.getString(cursor.getColumnIndex("title")),
                    calendar
                ).value();
            }
        }
        new File(dbPath).delete();
    }
}
