package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Source
import java.sql.ResultSet
import java.util.*

/**
 * Source to build Tag list from result set.
 */
class QueriedTags(private val resSource: Source<ResultSet>) : Source<List<Tag?>?> {
    override fun value(): List<Tag> {
        try {
            resSource.value().use { res ->
                val tags: MutableList<Tag> = ArrayList()
                while (res.next()) {
                    tags.add(ConstTag(
                        res.getLong("id"),
                        res.getString("title"),
                        res.getInt("version"),
                        res.getInt("delete") == 1)
                    )
                }
                return tags
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}