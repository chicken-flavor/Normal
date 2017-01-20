package com.ayyj.normaltemp.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ayyj on 2017/1/4.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String LOG_PATH = ROOT_PATH + "/crash/log/";
    private static final String LOG_SUFFIX = ".txt";//.log

    /**
     * if upload Exception info to Server
     */
    private boolean mUpload;

    public boolean isUpload() {
        return mUpload;
    }

    public void setUpload(boolean upload) {
        this.mUpload = upload;
    }


    //singleton pattern
    private static CrashHandler sInstance = new CrashHandler();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
    private Context mContext;

    public void init(Context context) {
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            leadExceptionToSD(e);
        } catch (Exception exc) {
            exc.printStackTrace();
            Log.e(TAG, "lead exception info failed!");
        }
        if (mUpload) {
            uploadExceptionToServer();
        }

        e.printStackTrace();
        if (mUncaughtExceptionHandler != null)
            mUncaughtExceptionHandler.uncaughtException(t, e);
        else {
            try {
                Toast.makeText(mContext, "3s to exit...", Toast.LENGTH_LONG);
                Thread.sleep(3 * 1000);
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * copy Exception info to sdCard
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void leadExceptionToSD(Throwable exc) throws Exception {
        if (!sdExist())
            return;
        File dir = new File(LOG_PATH);
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();

        String time = new SimpleDateFormat("yyyy-MM-DD_HH:mm:ss").format(new Date(System.currentTimeMillis()));
        File file = new File(LOG_PATH + time + LOG_SUFFIX);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        pw.println(time);
        getPhoneInfo(pw);
        pw.println();
        exc.printStackTrace(pw);
        if (pw != null) {
            pw.close();
        }
    }

    /**
     * upload Exception info to Server if necessary
     * (unrealized)
     */
    private void uploadExceptionToServer() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        //Android Version
        pw.println("OS Ver. : " + Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT);
        //App Version
        pw.println("App Ver. : " + pi.versionName + "_" + pi.versionCode);
        //handset makers
        pw.println("Maker : " + Build.MANUFACTURER);
        //phone model
        pw.println("Model : " + Build.MODEL);

        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        //IMEI
        pw.println("IMEI : " + imei);
        //CPU
        pw.println("CPU : " + Build.SUPPORTED_ABIS[0]);
    }

    /**
     * @return if sdCard is available
     */
    private boolean sdExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
