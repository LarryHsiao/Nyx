package com.larryhsiao.nyx.core.sync;

import javax.json.JsonObject;

public class JsonJwt implements Jwt {
    private final Syncs.Dest dest;
    private final JsonObject jwtObj;

    public JsonJwt(Syncs.Dest dest, JsonObject jwtObj) {
        this.dest = dest;
        this.jwtObj = jwtObj;
    }

    @Override
    public String id() {
        try {
            return jwtObj.getString("uid");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String accountId() {
        try {
            return jwtObj.getString("account_id");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String accessToken() {
        try {
            return "Bearer " + jwtObj.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Syncs.Dest type() {
        return dest;
    }
}
