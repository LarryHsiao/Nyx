package com.larryhsiao.nyx.sync;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larryhsiao.nyx.core.tags.NewJotTagById;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Action to sync to firebase.
 */
public class SyncTagJot implements Action {
    private final String userId;
    private final Source<Connection> db;

    public SyncTagJot(String userId, Source<Connection> db) {
        this.userId = userId;
        this.db = db;
    }

    @Override
    public void fire() {
        CollectionReference remote = FirebaseFirestore.getInstance()
            .collection(userId + "/data/tag_jot");
        remote.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sync(remote, task.getResult());
            }
        });
    }

    private void sync(CollectionReference remote, QuerySnapshot result) {
        Map<String, Map<String, Object>> dbTags = allDbTagJots();
        for (QueryDocumentSnapshot remoteTag : result) {
            syncTagJot(remoteTag, dbTags, remote);
        }
        dbTags.forEach((id, tag) -> updateRemoteTag(remote, id, tag));
    }

    private void syncTagJot(
        QueryDocumentSnapshot remoteTag,
        Map<String, Map<String, Object>> dbTags,
        CollectionReference remote
    ) {
        final String id = remoteTag.getId();
        final Map<String, Object> dbTag = dbTags.get(id);
        if (dbTag == null) {
            newLocalTag(remoteTag);
        } else {
            int dbVer = (int) dbTag.get("version");
            long remoteVersion = remoteTag.getLong("version");
            if (dbVer > remoteVersion) {
                updateRemoteTag(remote, id, dbTag);
            } else if (dbVer < remoteVersion) {
                updateLocalTag(remoteTag);
            }
            dbTags.remove(id);
        }
    }

    private void updateLocalTag(QueryDocumentSnapshot remoteTag) {
        try (PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "UPDATE TAG_JOT "
                + "SET VERSION = ?3, DELETE =?4 "
                + "WHERE JOT_ID=?1 AND TAG_ID=?2"
        )) {
            final long jotId = remoteTag.getLong("jot_id");
            final long tagId = remoteTag.getLong("tag_id");
            stmt.setLong(1, jotId);
            stmt.setLong(2, tagId);
            stmt.setInt(3, remoteTag.getLong("version").intValue());
            stmt.setInt(4, remoteTag.getLong("delete").intValue());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void newLocalTag(QueryDocumentSnapshot remoteTag) {
        new NewJotTagById(
            db,
            remoteTag.getLong("jot_id"),
            remoteTag.getLong("tag_id"),
            remoteTag.getLong("version").intValue(),
            remoteTag.getLong("delete").intValue()
        ).fire();
    }

    private void updateRemoteTag(
        CollectionReference tagRef,
        String id,
        Map<String, Object> tagJot
    ) {
        tagRef.document(id).set(tagJot);
    }

    private Map<String, Map<String, Object>> allDbTagJots() {
        try {
            ResultSet res = db.value().createStatement().executeQuery(
                // language=H2
                "SELECT * FROM TAG_JOT"
            );
            Map<String, Map<String, Object>> tagJots = new HashMap<>();
            while (res.next()) {
                Map<String, Object> properties = new HashMap<>();
                final long jotId = res.getLong("jot_id");
                final long tagId = res.getLong("tag_id");
                properties.put("jot_id", jotId);
                properties.put("tag_id", tagId);
                properties.put("version", res.getInt("version"));
                properties.put("delete", res.getInt("delete"));
                tagJots.put(jotId + "_" + tagId, properties);
            }
            return tagJots;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
