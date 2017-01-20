package com.ayyj.normaltemp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ayyj.normaltemp.R;

/**
 * Created by ayyj on 2017/1/19.
 */

public class MineAppWidgetProvider extends AppWidgetProvider {

    public static final String TAG = "MineAppWidgetProvider";
    public static final String CLICK_ACTION = "ACTION IS CLICK";

    public MineAppWidgetProvider() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e(TAG, "onReceive : action = " + intent.getAction());

        if (CLICK_ACTION.equals(intent.getAction()))
            Toast.makeText(context, "you've clicked!", Toast.LENGTH_LONG).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 3;
                    Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.demo, opts);

                    AppWidgetManager manager = AppWidgetManager.getInstance(context);
                    for (int i = 0; i < 37; i++) {
                        float degree = (i * 10) % 360;

                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
                        Bitmap bitmap = rotateBitmap(context, srcBitmap, degree);
                        remoteViews.setImageViewBitmap(R.id.imageview, bitmap);
                        Intent onClickIntent = new Intent();
                        onClickIntent.setAction(CLICK_ACTION);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, onClickIntent, 0);

                        remoteViews.setOnClickPendingIntent(R.id.imageview, pendingIntent);
//                        remoteViews.setOnClickFillInIntent(R.id.imageview, );

                        manager.updateAppWidget(new ComponentName(context, MineAppWidgetProvider.class), remoteViews);
                        SystemClock.sleep(30);
                    }
                }
            }).start();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.e(TAG, "onUpdate");

        final int count = appWidgetIds.length;
        for (int i = 0; i < count; i++) {
            int appWidgetId = appWidgetIds[i];
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    private void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.e(TAG, "appWidgetId = " + appWidgetId);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        Intent onClickTntent = new Intent();
        onClickTntent.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, onClickTntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.imageview, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    /**
     *
     * @param context
     * @param srcBitmap
     * @param degree
     * @return
     */
    private Bitmap rotateBitmap(Context context, Bitmap srcBitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }
}
