package com.larryhsiao.nyx.account;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larryhsiao.nyx.core.tags.AllTags;
import com.larryhsiao.nyx.core.tags.ConstTag;
import com.larryhsiao.nyx.core.tags.NewTagById;
import com.larryhsiao.nyx.core.tags.QueriedTags;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.UpdateTag;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Action to sync firebase
 */
public class SyncTags implements Action {
    private final String userId;
    private final Source<Connection> db;

    public SyncTags(String userId, Source<Connection> db) {
        this.userId = userId;
        this.db = db;
    }

    @Override
    public void fire() {
        CollectionReference remote = FirebaseFirestore.getInstance().collection(userId + "/data/tags");
        remote.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sync(remote, task.getResult());
            }
        });
    }

    private void sync(CollectionReference remote, QuerySnapshot result) {
        Map<String, Tag> dbTags = new HashMap<>();
        for (Tag tag : new QueriedTags(new AllTags(db, true)).value()) {
            dbTags.put(tag.id() + "", tag);
        }
        for (QueryDocumentSnapshot remoteTag : result) {
            final Tag dbTag = dbTags.get(remoteTag.getId());
            if (dbTag == null) {
                newLocalTag(remoteTag);
            } else {
                long remoteVersion = remoteTag.getLong("version");
                if (dbTag.version() > remoteVersion) {
                    updateRemoteTag(remote, dbTag);
                } else if (dbTag.version() < remoteVersion) {
                    updateLocalTag(remoteTag);
                }
                dbTags.remove(dbTag.id() + "");
            }
        }
        dbTags.forEach((s, tag) -> {
            updateRemoteTag(remote, tag);
        });
    }

    private void updateLocalTag(QueryDocumentSnapshot remoteTag) {
        new UpdateTag(
            db,
            new ConstTag(
                Long.parseLong(remoteTag.getId()),
                remoteTag.getString("title"),
                remoteTag.getLong("version").intValue(),
                remoteTag.getLong("delete").intValue() == 1
            ), false
        ).fire();
    }

    private void newLocalTag(QueryDocumentSnapshot remoteTag) {
        new NewTagById(
            db,
            new ConstTag(
                Long.parseLong(remoteTag.getId()),
                remoteTag.getString("title"),
                remoteTag.getLong("version").intValue(),
                remoteTag.getLong("delete") == 1
            )
        ).value();
    }

    private void updateRemoteTag(CollectionReference tagRef, Tag tag) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", tag.title());
        data.put("version", tag.version());
        data.put("delete", tag.deleted() ? 1 : 0);
        tagRef.document(tag.id() + "").set(data);
    }
}
