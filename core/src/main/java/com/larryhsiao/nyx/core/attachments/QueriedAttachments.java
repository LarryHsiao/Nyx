package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Source;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Source to build Attachment list from query source.
 */
public class QueriedAttachments implements Source<List<Attachment>> {
    private final Source<ResultSet> query;

    public QueriedAttachments(Source<ResultSet> query) {
        this.query = query;
    }

    @Override
    public List<Attachment> value() {
        try (ResultSet res = query.value()) {
            List<Attachment> attachments = new ArrayList<>();
            while (res.next()) {
                attachments.add(new ConstAttachment(
                    res.getLong("id"),
                    res.getLong("jot_id"),
                    res.getString("uri"),
                    res.getInt("version"),
                    res.getInt("delete")
                ));
            }
            return attachments;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
