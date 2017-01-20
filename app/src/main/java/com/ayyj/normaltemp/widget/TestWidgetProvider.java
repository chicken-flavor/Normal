package com.ayyj.normaltemp.widget;

/**
 * Created by yangyongjun on 2017/1/20.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ayyj.normaltemp.R;

/**
 * AppWidgetProvider子类
 *
 * @author qingtian 2014年11月21日15:02:53
 */
public class TestWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "qingtian";
    // 定义一个常量字符串，该常量用于命名Action
    private static final String UPDATA_STATUS_FROM_WIDGET_START = "net.qingtian.UPDATA_STATUS_FROM_WIDGET_START";
    private static final String UPDATA_STATUS_FROM_WIDGET_STOP = "net.qingtian.UPDATA_STATUS_FROM_WIDGET_STOP";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        String action = intent.getAction();
        if (UPDATA_STATUS_FROM_WIDGET_START.equals(action)) {
            setViewStatus(context, 1);
        } else if (UPDATA_STATUS_FROM_WIDGET_STOP.equals(action)) {
            setViewStatus(context, 0);
        } else {
            super.onReceive(context, intent);// 这里一定要添加else部分，不然，onReceive不会去调用其它的方法。但是如果把这条语句放在外面，就会每次运行onUpdate,onDeleted等方法，就会运行两次，因为UPDATE_ACTION.equals(action)配置成功会运行一次，super.onReceive(context,
            // intent配置成功又会运行一次，后都是系统自定义的。
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.i(TAG, "onupdated");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        // 设置开始监听
        Intent intentStart = new Intent();
        // 为Intent对象设置Action
        intentStart.setAction(UPDATA_STATUS_FROM_WIDGET_START);
        // 使用getBroadcast方法，得到一个PendingIntent对象，当该对象执行时，会发送一个广播
        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(context,
                0, intentStart, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_start,
                pendingIntentStart);

        // 设置停止监听
        Intent intentStop = new Intent();
        // 为Intent对象设置Action
        intentStop.setAction(UPDATA_STATUS_FROM_WIDGET_STOP);
        // 使用getBroadcast方法，得到一个PendingIntent对象，当该对象执行时，会发送一个广播
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(context,
                0, intentStop, 0);
        remoteViews
                .setOnClickPendingIntent(R.id.widget_stop, pendingIntentStop);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled");
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled");
        super.onEnabled(context);
    }

    /**
     * 设置widget的状态，即改变textView里面的文字
     *
     * @param status
     */
    public void setViewStatus(Context context, int status) {
        RemoteViews remoteViews = null;
        if (status == 0) {// 结束
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.widget_text, "0      结束");
        } else if (status == 1) {// 开始
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.widget_text, "1      开始");
        } else {
            return;
        }
        // remoteViews.setTextViewText(R.id.test_text, "data  "+data);
        // getInstance(Context context) Get the AppWidgetManager instance to
        // use for the supplied Context object.静态方法。
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        ComponentName componentName = new ComponentName(context,
                TestWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

}