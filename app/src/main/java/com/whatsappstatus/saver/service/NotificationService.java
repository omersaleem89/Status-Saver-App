package com.whatsappstatus.saver.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.whatsappstatus.saver.HelperMethods;
import com.whatsappstatus.saver.R;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    public static final String INTENT_NOTIFY = "com.whatsappstatus.saver.services.INTENT_NOTIFY";
    private static int count = 0;
    public static Builder notification;
    final String UNLOCK_BUSINESS_STATUSES = "business_statuses";
    private HelperMethods helperMethods;
    private final IBinder mBinder = new ServiceBinder(this);
    private NotificationManager mNM;
    private Notification notif;

    private class FileObserver extends TimerTask {
        private final NotificationService notificationService;

        @Override
        public void run() {
            CheckForNewFiles();


        }

        public FileObserver(NotificationService notificationService) {
            this.notificationService = notificationService;
        }
    }

    public class ServiceBinder extends Binder {
        private final NotificationService notificationService;


        NotificationService getService() {
            return notificationService;
        }

        public ServiceBinder(NotificationService notificationService) {
            this.notificationService = notificationService;
        }
    }

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        this.helperMethods = new HelperMethods(this);
        this.mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        new Timer().scheduleAtFixedRate(new FileObserver(this), (long) 0, (long) 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i("LocalService", new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("Received start id ").append(i2).toString()).append(": ").toString()).append(intent).toString());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Intent intent = new Intent(this, Class.forName("com.whatsappstatus.saver.service.reciever.BootReceiver"));
            intent.setAction("com.whatsappstatus.notif.onDestroyed");
            sendBroadcast(intent);
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static Builder getBuilder() {
        return notification;
    }

    private void showImageStatusNotification() {
        String stringBuffer = new StringBuffer().append(Environment.getExternalStorageDirectory().getAbsolutePath()).append("/WhatsApp/Media/.Statuses/").toString();
        notification = new Builder(this);
        try {
            notification.setContentTitle("Status Saver").setContentText("New Image Status, Refresh Now!").setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, Class.forName("com.whatsappstatus.saver.MainActivity")), 0)).setSmallIcon(R.drawable.notif).setShowWhen(true).setVibrate(new long[]{(long) 100, (long) 100, (long) 100, (long) 100}).setColor(ContextCompat.getColor(this, R.color.colorAccent));
            this.notif = notification.getNotification();
            Notification notification = this.notif;
            notification.flags |= 8;
            this.mNM.notify(0, this.notif);
            getSharedPreferences("latestFile", 0).edit().putLong("latestFile", HelperMethods.getLatestFilefromDir(stringBuffer).lastModified()).apply();
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    private void showVideoStatusNotification() {
        String stringBuffer = new StringBuffer().append(Environment.getExternalStorageDirectory().getAbsolutePath()).append("/WhatsApp/Media/.Statuses/").toString();
        notification = new Builder(this);
        try {
            notification.setContentTitle("Status Saver").setContentText("New Video Status, Refresh Now!").setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, Class.forName("com.whatsappstatus.saver.MainActivity")), 0)).setSmallIcon(R.drawable.notif).setShowWhen(true).setVibrate(new long[]{(long) 100, (long) 100, (long) 100, (long) 100}).setColor(ContextCompat.getColor(this, R.color.colorAccent));
            this.notif = notification.getNotification();
            Notification notification = this.notif;
            notification.flags |= 8;
            this.mNM.notify(1, this.notif);
            getSharedPreferences("latestFile", 0).edit().putLong("latestFile", HelperMethods.getLatestFilefromDir(stringBuffer).lastModified()).apply();
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public void CheckForNewFiles() {
        int i = 0;
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0 && checkSelfPermission == 0) {
            File[] fileArr = (File[]) null;
            String stringBuffer = new StringBuffer().append(Environment.getExternalStorageDirectory().getAbsolutePath()).append("/WhatsApp/Media/.Statuses/").toString();
            File file = new File(stringBuffer);
            if (file.isFile()) {
                file.delete();
            }
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            if (!file.exists()) {
                file.mkdirs();
            }
            if (file.isDirectory()) {
                try {
                    fileArr = new File(stringBuffer).listFiles();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            Date date = new Date(getSharedPreferences("latestFile", 0).getLong("latestFile", System.currentTimeMillis()));
            if (fileArr != null) {
                count = fileArr.length;
                Log.d("Status Files Count", new StringBuffer().append("").append(count).toString());
            }
            if (fileArr != null && count > 0) {
                while (i < fileArr.length) {
                    File file2 = fileArr[i];
                    if (new Date(file2.lastModified()).compareTo(date) > 0) {
                        if (file2.getName().endsWith(".jpg") || file2.getName().endsWith(".jpeg") || file2.getName().endsWith(".png")) {
                            showImageStatusNotification();
                        }
                        if (file2.getName().endsWith(".mp4") || file2.getName().endsWith(".avi") || file2.getName().endsWith(".mkv") || file2.getName().endsWith(".gif")) {
                            showVideoStatusNotification();
                        }
                    }
                    i++;
                }
            }
        }
    }
}
