package com.larryhsiao.nyx.web

import android.content.Context
import com.larryhsiao.nyx.ConfigImpl
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.web.diaries.TkDiaries
import com.larryhsiao.nyx.web.diaries.TkDiaryById
import com.larryhsiao.nyx.web.diaries.TkDiaryDeleteById
import com.larryhsiao.nyx.web.diaries.TkDiaryNew
import com.larryhsiao.nyx.web.files.TkFileById
import com.larryhsiao.nyx.web.files.TkFileUpload
import com.larryhsiao.nyx.web.files.TkFiles
import com.larryhsiao.nyx.web.media.TkMedia
import com.silverhetch.clotho.Source
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.takes.facets.fork.*
import org.takes.http.Exit
import org.takes.http.FtBasic
import java.io.File


/**
 * Access implementation with [takes](https://github.com/yegor256/takes)
 */
class TakesAccess(
    context: Context,
    private val db: RDatabase
) : WebAccess {
    companion object {
        private const val PORT = 8080
    }

    private val config = ConfigImpl(context)
    private var enabled = false
    private var exit = Exit { enabled.not() }
    private val context: Context = context.applicationContext

    override fun enable() {
        if (enabled) {
            return
        }
        enabled = true
        GlobalScope.launch {
            FtBasic(
                TkFork(
                    ArrayList<Fork>().apply {
                        addAll(diaries())
                        addAll(media())
                        addAll(files())
                    }
                ),
                PORT
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
    class Singleton(private val context: Context, private val db: RDatabase) :
        Source<WebAccess> {
        companion object {
            private lateinit var instance: WebAccess

            private fun obtain(context: Context, db: RDatabase): WebAccess {
                if (::instance.isInitialized) {
                    return instance
                }
                return TakesAccess(context, db).apply { instance = this }
            }
        }

        override fun value(): WebAccess {
            return obtain(context, db)
        }
    }

    private fun diaries(): List<Fork> {
        return listOf(
            FkRegex(
                "/",
                TkAndroidAssets(
                    context,
                    "http://localhost:$PORT",
                    "index.html",
                    "text/html"
                )
            ),
            FkRegex(
                "/tacit-css.min.css",
                TkAndroidAssets(
                    context,
                    "http://localhost:$PORT",
                    "tacit-css.min.css",
                    "text/css"
                )
            ),
            FkRegex(
                "/diaries",
                TkFork(
                    FkMethods("GET", TkDiaries(db)),
                    FkMethods("POST", TkDiaryNew(context, config ,db))
                )
            ),
            FkRegex(
                "^/diaries/\\d+\$",
                TkFork(
                    FkMethods("GET", TkDiaryById(db)),
                    FkMethods("DELETE", TkDiaryDeleteById(db))
                )
            )
        )
    }

    private fun media(): List<Fork> {
        return listOf(
            FkRegex("/media", TkMedia(db))
        )
    }

    private fun files(): List<Fork> {
        return listOf(
            FkRegex(
                "/files",
                TkFork(
                    FkMethods("POST", TkFileUpload(config.mediaRoot())),
                    FkMethods("GET", TkFiles(config.mediaRoot()))
                )
            ),
            FkRegex(
                "/files/\\S+\$",
                TkFork(
                    FkMethods("GET", TkFileById(config.mediaRoot()))
                )
            )
        )
    }
}