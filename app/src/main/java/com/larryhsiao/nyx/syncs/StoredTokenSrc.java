package com.larryhsiao.nyx.syncs;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.storage.Ceres;
import com.larryhsiao.nyx.core.sync.Syncs;

import java.util.HashMap;
import java.util.Map;

public class StoredTokenSrc implements Source<Map<Syncs.Dest, String>> {
    private final Ceres ceres;

    public StoredTokenSrc(Ceres ceres) {this.ceres = ceres;}

    @Override
    public Map<Syncs.Dest, String> value() {
        final Map<Syncs.Dest, String> result = new HashMap<>();
        for (Syncs.Dest value : Syncs.Dest.values()) {
            final String token = ceres.get(value.name());
            if (token.length()!=0) {
                result.put(value, token);
            }
        }
        return result;
    }
}
