package com.larryhsiao.nyx;

import android.os.Environment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.larryhsiao.nyx.attachments.NewAttachment;
import com.larryhsiao.nyx.jots.AllJots;
import com.larryhsiao.nyx.jots.Jot;
import com.larryhsiao.nyx.jots.NewJot;
import com.larryhsiao.nyx.jots.QueriedJots;
import com.larryhsiao.nyx.jots.UpdateJot;
import com.larryhsiao.nyx.jots.UpdatedJot;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.file.FileText;
import com.silverhetch.clotho.source.ConstSource;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

/**
 * Action for importing backup.
 */
public class ImportFromBackup implements Action {
    private final Source<Connection> db;

    public ImportFromBackup(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public void fire() {
        if (new QueriedJots(new AllJots(db)).value().size() != 0) {
            return;
        }
        File root = new File(
            new File(
                Environment.getExternalStorageDirectory(),
                "jotted_backup"
            ), "201910281931"
        );
        JsonArray diaryArr = new JsonParser().parse(
            new FileText(new File(root, "diary.json")).value()
        ).getAsJsonArray();

        JsonArray mediaArr = new JsonParser().parse(
            new FileText(new File(root, "media.json")).value()
        ).getAsJsonArray();

        for (JsonElement jsonElement : diaryArr) {
            JsonObject obj = jsonElement.getAsJsonObject();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(obj.get("c").getAsLong()));
            Jot jot = new NewJot(db, obj.get("b").getAsString(), calendar).value();
            System.out.println(jsonElement.toString());

            long jotId = obj.get("a").getAsLong();

            for (JsonElement media : mediaArr) {
                JsonObject mediaObjJson = media.getAsJsonObject();
                JsonObject mediaObj = mediaObjJson.get("media").getAsJsonObject();
                String uri = mediaObj.get("c").getAsString();
                long id = mediaObj.get("b").getAsLong();
                if (id!= jotId){
                    continue;
                }
                if (uri.startsWith("geo")) {
                    new UpdateJot(
                        new UpdatedJot(
                            jot, jot.content(), new ConstSource<>(new double[]{
                            Double.parseDouble(uri.replace("geo:", "").split(",")[1]),
                            Double.parseDouble(uri.replace("geo:", "").split(",")[0])
                        })), db
                    ).fire();
                } else {
                    String fileName = mediaObjJson.get("exportedFileName").getAsString() + ".jpg";
                    File file = new File(
                        new File(
                            new File(
                                Environment.getExternalStorageDirectory(),
                                "DCIM"
                            ),
                            "100ANDRO"
                        ), fileName
                    );
                    System.out.println("abc----- "+ file.exists());
                    new NewAttachment(
                        db,
                        file.toURI().toASCIIString(),
                        jot.id()
                    ).value();
                }
            }
        }
    }
}
