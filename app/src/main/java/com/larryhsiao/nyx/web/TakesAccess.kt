package com.larryhsiao.nyx.web

import android.content.Context
import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source
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

    override fun isRunning(): Boolean {
        return enabled
    }

    /**
     * Source to build [WebAccess].
     */
    class Singleton(private val db: RDatabase) : Source<WebAccess> {
        companion object {
            private lateinit var instance: WebAccess

            private fun obtain(db: RDatabase): WebAccess {
                if (::instance.isInitialized) {
                    return instance
                }
                return TakesAccess(db).apply { instance = this }
            }
        }

        override fun value(): WebAccess {
            return obtain(db)
        }
    }
}