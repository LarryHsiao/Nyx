package com.larryhsiao.nyx.core.metadata

import com.larryhsiao.nyx.core.metadata.Metadata.Type
import com.larryhsiao.clotho.Source
import java.sql.ResultSet
import java.util.*

class QueriedMetadata(
    private val query: Source<ResultSet>
) : Source<List<Metadata>> {
    override fun value(): List<Metadata> {
        query.value().use { res ->
            val metadata: MutableList<Metadata> = ArrayList()
            while (res.next()) {
                metadata.add(ConstMetadata(
                    res.getLong("id"),
                    res.getLong("jot_id"),
                    Type.valueOf(res.getString("type")),
                    res.getString("value"),
                    res.getString("title"),
                    res.getBigDecimal("value_decimal"),
                    res.getString("comment"),
                    res.getLong("version"),
                    res.getInt("deleted") == 1
                ))
            }
            return metadata
        }
    }
}