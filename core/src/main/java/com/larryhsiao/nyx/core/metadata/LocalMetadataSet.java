package com.larryhsiao.nyx.core.metadata;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.metadata.openweather.LocalWeathers;
import com.larryhsiao.nyx.core.metadata.openweather.Weathers;

import java.sql.Connection;
import java.util.List;

public class LocalMetadataSet implements MetadataSet {
    private final Source<Connection> db;

    public LocalMetadataSet(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public List<Metadata> byJotId(long id) {
        return new QueriedMetadata(
            new MetadataByJotId(db, id, false)
        ).value();
    }

    @Override
    public void deleteByJotId(long id) {
        new MetadataDeletionByJotId(db, id).fire();
    }

    @Override
    public Weathers weathers() {
        return new LocalWeathers(db);
    }

    @Override
    public List<Metadata> all() {
        return new QueriedMetadata(
            new AllMetadata(db, true)
        ).value();
    }

    @Override
    public void createWithId(Metadata metadata) {
        new NewMetadataWithId(db, metadata).fire();
    }

    @Override
    public void replace(Metadata metadata) {
        new UpdatedMetadata(db, metadata, false).value();
    }
}
