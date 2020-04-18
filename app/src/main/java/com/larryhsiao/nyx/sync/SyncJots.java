package com.larryhsiao.nyx.sync;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.NewJotById;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.jots.UpdateJot;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Action to sync to firebase.
 */
public class SyncJots implements Action {
    private final String userId;
    private final Source<Connection> db;

    public SyncJots(String userId, Source<Connection> db) {
        this.userId = userId;
        this.db = db;
    }

    @Override
    public void fire() {
        CollectionReference remoteJots = FirebaseFirestore.getInstance()
            .collection(userId + "/data/jots");
        remoteJots.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sync(remoteJots, task.getResult());
            }
        });
    }

    private void sync(CollectionReference remoteJots, QuerySnapshot result) {
        Map<String, Jot> dbJots = new HashMap<>();
        for (Jot jot : new QueriedJots(new AllJots(db, true)).value()) {
            dbJots.put(jot.id() + "", jot);
        }
        for (QueryDocumentSnapshot remoteJot : result) {
            syncJot(dbJots, remoteJot, remoteJots);
        }
        dbJots.forEach((s, jot) -> {
            updateRemoteJot(remoteJots, jot);
        });
    }

    private void syncJot(
        Map<String, Jot> dbJots,
        QueryDocumentSnapshot remoteJot,
        CollectionReference remoteJots
    ) {
        final Jot dbJot = dbJots.get(remoteJot.getId());
        if (dbJot == null) {
            newLocalJot(remoteJot);
        } else {
            long remoteVersion = remoteJot.getLong("version");
            if (dbJot.version() > remoteVersion) {
                updateRemoteJot(remoteJots, dbJot);
            } else if (dbJot.version() < remoteVersion) {
                updateLocalJot(remoteJot);
            }
            dbJots.remove(dbJot.id() + "");
        }
    }

    private void updateLocalJot(QueryDocumentSnapshot remoteJot) {
        GeoPoint geo = remoteJot.getGeoPoint("location");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(remoteJot.getLong("createdTime")));
        new UpdateJot(
            new ConstJot(
                Long.parseLong(remoteJot.getId()),
                remoteJot.getString("content"),
                calendar.getTimeInMillis(),
                new double[]{geo.getLongitude(), geo.getLatitude()},
                remoteJot.getString("mood"),
                remoteJot.getLong("version").intValue(),
                remoteJot.getLong("delete").intValue() == 1
            ), db, false
        ).fire();
    }

    private void newLocalJot(QueryDocumentSnapshot remoteJot) {
        GeoPoint geo = remoteJot.getGeoPoint("location");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(remoteJot.getLong("createdTime")));
        new NewJotById(
            db,
            new ConstJot(
                Long.parseLong(remoteJot.getId()),
                remoteJot.getString("content"),
                calendar.getTimeInMillis(),
                new double[]{geo.getLongitude(), geo.getLatitude()},
                remoteJot.getString("mood"),
                remoteJot.getLong("version").intValue(),
                remoteJot.getLong("delete").intValue() == 1
            )
        ).value();
    }

    private void updateRemoteJot(CollectionReference jotsRef, Jot jot) {
        Map<String, Object> data = new HashMap<>();
        data.put("content", jot.content());
        data.put("mood", jot.mood());
        data.put("createdTime", jot.createdTime());
        data.put("location", new GeoPoint(jot.location()[1], jot.location()[0]));
        data.put("version", jot.version());
        data.put("delete", jot.deleted() ? 1 : 0);
        jotsRef.document(jot.id() + "").set(data);
    }
}
