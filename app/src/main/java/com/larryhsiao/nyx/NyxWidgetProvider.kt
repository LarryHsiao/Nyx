package com.larryhsiao.nyx

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.widget.RemoteViews
import com.larryhsiao.nyx.jot.InstantJotActivity

/**
 * Widget provider for Nyx.
 */
class NyxWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        super.onUpdate(context, manager, ids)
        for (id in ids) {
            manager.updateAppWidget(
                id,
                RemoteViews(context.packageName, R.layout.widget_nyx).apply {
                    setOnClickPendingIntent(
                        R.id.widgetNyx_addingImageView,
                        PendingIntent.getActivity(
                            context,
                            0,
                            Intent(context, InstantJotActivity::class.java),
                            0
                        )
                    )
                }
            )
        }
    }
}