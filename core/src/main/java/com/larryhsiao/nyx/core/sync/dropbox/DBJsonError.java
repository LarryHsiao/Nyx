package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Source;

import javax.json.Json;
import java.io.StringReader;

public class DBJsonError implements Source<String> {
    private final Source<String> jsonStr;

    public DBJsonError(Source<String> jsonStr) {
        this.jsonStr = jsonStr;
    }

    @Override
    public String value() {
        String json = jsonStr.value();
        try {
            return Json.createReader(new StringReader(json))
                .readObject()
                .getString("error_summary");
        } catch (Exception e) {
            e.printStackTrace();
            return json;
        }
    }
}
