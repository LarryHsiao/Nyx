package com.larryhsiao.nyx.jot.map

import com.larryhsiao.clotho.Source
import java.text.SimpleDateFormat
import java.util.*

class DateRangeText(
    private val from: Source<Calendar>,
    private val to: Source<Calendar>
) : Source<String> {
    override fun value(): String {
        return SimpleDateFormat.getDateInstance()
            .format(from.value().time) + "-" + SimpleDateFormat.getDateInstance()
            .format(to.value().time)
    }
}