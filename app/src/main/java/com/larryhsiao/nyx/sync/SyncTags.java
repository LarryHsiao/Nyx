package com.larryhsiao.nyx.sync;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larryhsiao.nyx.core.tags.*;
import com.larryhsiao.nyx.sync.encryption.StringEncryptor;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

/**
 * Action to sync to firebase.
 */
public class SyncTags implements Action {
    private final DocumentReference dataRef;
    private final Source<Connection> db;
    private final StringEncryptor encryptor;

    public SyncTags(
        DocumentReference dataRef,
        Source<Connection> db,
        StringEncryptor encrypt) {
        this.dataRef = dataRef;
        this.db = db;
        this.encryptor = encrypt;
    }

    @Override
    public void fire() {
        CollectionReference remote = dataRef.collection("tags");
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
            syncTag(dbTags, remoteTag, remote);
        }
        dbTags.forEach((s, tag) -> {
            updateRemoteTag(remote, tag);
        });
    }

    private void syncTag(
        Map<String, Tag> dbTags,
        QueryDocumentSnapshot remoteTag,
        CollectionReference remote
    ) {
        final Tag dbTag = dbTags.get(remoteTag.getId());
        if (dbTag == null) {
            newLocalTag(remoteTag);
        } else {
            long remoteVersion = Long.parseLong(encryptor.decrypt(remoteTag.getString("version")));
            if (dbTag.version() > remoteVersion) {
                updateRemoteTag(remote, dbTag);
            } else if (dbTag.version() < remoteVersion) {
                updateLocalTag(remoteTag);
            }
            dbTags.remove(dbTag.id() + "");
        }
    }

    private void updateLocalTag(QueryDocumentSnapshot remoteTag) {
        new UpdateTag(
            db,
            new ConstTag(
                parseLong(remoteTag.getId()),
                encryptor.decrypt(remoteTag.getString("title")),
                parseInt(encryptor.decrypt(remoteTag.getString("version"))),
                parseInt(encryptor.decrypt(remoteTag.getString("delete"))) == 1
            ), false
        ).fire();
    }

    private void newLocalTag(QueryDocumentSnapshot remoteTag) {
        new NewTagById(
            db,
            new ConstTag(
                parseLong(remoteTag.getId()),
                encryptor.decrypt(remoteTag.getString("title")),
                parseInt(encryptor.decrypt(remoteTag.getString("version"))),
                parseInt(encryptor.decrypt(remoteTag.getString("delete"))) == 1
            )
        ).value();
    }

    private void updateRemoteTag(CollectionReference tagRef, Tag tag) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", encryptor.encrypt(tag.title()));
        data.put("version", encryptor.encrypt(tag.version() + ""));
        data.put("delete", encryptor.encrypt((tag.deleted() ? 1 : 0) + ""));
        tagRef.document(tag.id() + "").set(data);
    }
}
