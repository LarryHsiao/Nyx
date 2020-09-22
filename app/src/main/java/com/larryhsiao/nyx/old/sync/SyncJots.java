package com.larryhsiao.nyx.old.sync;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larryhsiao.nyx.core.jots.*;
import com.larryhsiao.nyx.old.sync.encryption.Encryptor;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.tasks.Tasks.await;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

/**
 * Action to sync to firebase.
 */
public class SyncJots implements Action {
    private final DocumentReference dataRef;
    private final Source<Connection> db;
    private final Encryptor<String> encryptor;

    public SyncJots(
        DocumentReference dataRef,
        Source<Connection> db,
        Encryptor<String> encryptor
    ) {
        this.dataRef = dataRef;
        this.db = db;
        this.encryptor = encryptor;
    }

    @Override
    public void fire() {
        try {
            CollectionReference remoteJots = dataRef.collection("jots");
            Task<QuerySnapshot> task = remoteJots.get();
            QuerySnapshot snapshot = await(task);
            if (task.isSuccessful()) {
                sync(remoteJots, snapshot);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sync(CollectionReference remoteJots, QuerySnapshot result) {
        Map<String, Jot> dbJots = new HashMap<>();
        for (Jot jot : new QueriedJots(new AllJots(db, true)).value()) {
            dbJots.put(jot.id() + "", jot);
        }
        for (QueryDocumentSnapshot remoteJot : result) {
            syncJot(dbJots, remoteJot, remoteJots);
        }
        dbJots.forEach((s, jot) -> updateRemoteJot(remoteJots, jot));
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
            long remoteVersion = parseLong(encryptor.decrypt(remoteJot.getString("version")));
            if (dbJot.version() > remoteVersion) {
                updateRemoteJot(remoteJots, dbJot);
            } else if (dbJot.version() < remoteVersion) {
                updateLocalJot(remoteJot);
            }
            dbJots.remove(dbJot.id() + "");
        }
    }

    private void updateLocalJot(QueryDocumentSnapshot remoteJot) {
        String title = encryptor.decrypt(remoteJot.getString("title"));
        if (title == null){
            title = "";
        }
        new UpdateJot(
            new ConstJot(
                parseLong(remoteJot.getId()),
                title,
                encryptor.decrypt(remoteJot.getString("content")),
                parseLong(
                    encryptor.decrypt(remoteJot.getString("createdTime"))),
                new PointSource(
                    encryptor.decrypt(remoteJot.getString("location"))
                ).value(),
                encryptor.decrypt(remoteJot.getString("mood")),
                parseInt(encryptor.decrypt(remoteJot.getString("version"))),
                parseInt(encryptor.decrypt(remoteJot.getString("delete"))) == 1
            ), db, false
        ).fire();
    }

    private void newLocalJot(QueryDocumentSnapshot remoteJot) {
        String title = encryptor.decrypt(remoteJot.getString("title"));
        if (title == null){
            title = "";
        }
        new NewJotById(
            db,
            new ConstJot(
                parseLong(remoteJot.getId()),
                title,
                encryptor.decrypt(remoteJot.getString("content")),
                parseLong(
                    encryptor.decrypt(remoteJot.getString("createdTime"))),
                new PointSource(
                    encryptor.decrypt(remoteJot.getString("location"))
                ).value(),
                encryptor.decrypt(remoteJot.getString("mood")),
                parseInt(encryptor.decrypt(remoteJot.getString("version"))),
                parseInt(encryptor.decrypt(remoteJot.getString("delete"))) == 1
            )
        ).value();
    }

    private void updateRemoteJot(CollectionReference jotsRef, Jot jot) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("title", encryptor.encrypt(jot.title()));
            data.put("content", encryptor.encrypt(jot.content()));
            data.put("mood", encryptor.encrypt(jot.mood()));
            data.put("createdTime", encryptor.encrypt(jot.createdTime() + ""));
            data.put("location", encryptor.encrypt(
                new Point(
                    new CoordinateArraySequence(
                        new Coordinate[]{
                            new Coordinate(jot.location()[0], jot.location()[1])
                        }
                    ), new GeometryFactory()
                ).toText()
            ));
            data.put("version", encryptor.encrypt(jot.version() + ""));
            data.put("delete", encryptor.encrypt((jot.deleted() ? 1 : 0) + ""));
            await(jotsRef.document(jot.id() + "").set(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
