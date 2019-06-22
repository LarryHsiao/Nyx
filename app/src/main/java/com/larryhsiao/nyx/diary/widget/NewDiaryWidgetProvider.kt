package com.larryhsiao.nyx.diary.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.pages.NewDiaryActivity

/**
 * Provider for widget new diary.
 */
class NewDiaryWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds.forEach { widgetId ->
            appWidgetManager.updateAppWidget(
                widgetId,
                RemoteViews(
                    context.packageName,
                    R.layout.widget_new_diary
                ).also { view ->
                    view.setOnClickPendingIntent(
                        R.id.widgetNewDiary_create,
                        PendingIntent.getActivity(
                            context,
                            0,
                            Intent(context, NewDiaryActivity::class.java),
                            0
                        )
                    )
                })
        }
    }
}
