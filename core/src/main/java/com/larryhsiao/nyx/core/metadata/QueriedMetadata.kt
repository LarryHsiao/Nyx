package com.larryhsiao.nyx.core.metadata

import com.silverhetch.clotho.Source
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
                    res.getString("type"),
                    res.getString("title"),
                    res.getString("content"),
                    res.getLong("version")
                ))
            }
            return metadata
        }
    }
}