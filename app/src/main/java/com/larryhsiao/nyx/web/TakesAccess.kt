package com.larryhsiao.nyx.web

import com.larryhsiao.nyx.database.RDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.takes.facets.fork.FkMethods
import org.takes.facets.fork.FkRegex
import org.takes.facets.fork.TkFork
import org.takes.http.Exit
import org.takes.http.FtBasic


/**
 * Access implementation with [takes](https://github.com/yegor256/takes)
 */
class TakesAccess(private val db: RDatabase) : WebAccess {
    private var enabled = false
    private var exit = Exit { enabled.not() }

    override fun enable() {
        if (enabled) {
            return
        }
        enabled = true
        GlobalScope.launch {
            FtBasic(
                TkFork(
                    FkRegex(
                        "/diaries",
                        TkFork(
                            FkMethods("GET", TkDiaries(db))
                        )
                    )
                ),
                8080
            ).start(exit)
        }
    }

    override fun disable() {
        if (enabled.not()) {
            return
        }
        enabled = false
    }
}