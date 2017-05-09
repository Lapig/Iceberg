package xyz.lapig.iceberg;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.text.Html;
import android.text.Spanned;
/**
 * Widget
 */
public class IcebergWidget extends AppWidgetProvider {
    public static final String ACTION_TEXT_CHANGED = "xyz.lapig.iceberg.TEXT_CHANGED";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) 
    {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.iceberg_widget);

        // Instruct the widget manager to update the widget

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.iceberg_widget);
        if (intent.getAction().equals(ACTION_TEXT_CHANGED)) {
            // handle intent here
            String s = intent.getStringExtra("updatedWidgetText");
			Spanned formattedStr = Html.fromHtml(s, Html.FROM_HTML_OPTION_USE_CSS_COLORS);
            views.setTextViewText(R.id.appwidget_text, formattedStr);
        }
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(new ComponentName(context,IcebergWidget.class.getName()), views);
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
