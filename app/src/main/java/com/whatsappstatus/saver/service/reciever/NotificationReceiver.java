package com.whatsappstatus.saver.service.reciever;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.whatsappstatus.saver.HelperMethods;
import com.whatsappstatus.saver.R;
import java.io.File;

public class NotificationReceiver extends BroadcastReceiver {
    private final String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private File[] allFiles = new File(this.targetPath).listFiles();
    private final String targetPath = new StringBuffer().append(this.ExternalStorageDirectoryPath).append("/WhatsApp/Media/.Statuses/").toString();

    public class TransferAll extends AsyncTask<Void, String, Boolean> {
        private Context context;
        private NotificationManager notifManager;
        private final NotificationReceiver notifActionReceiver;

        public TransferAll(NotificationReceiver notifActionReceiver, Context context, NotificationManager notificationManager) {
            this.notifActionReceiver = notifActionReceiver;
            this.notifManager = notificationManager;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            this.notifManager.cancel(0);
            this.notifManager.cancelAll();
            Builder builder = new Builder(this.context);
            builder.setContentText("Downloading Statuses");
            builder.setColor(ContextCompat.getColor(this.context, R.color.colorAccent));
            builder.setSmallIcon(R.drawable.notif);
            builder.setProgress(100, 30, true);
            builder.setOngoing(true);
            this.notifManager.notify(420, builder.build());
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voidArr) {
            try {
                File[] allFiles = this.notifActionReceiver.allFiles;
                for (File file : allFiles) {
                    String str = file.getName().toString();
                    if (str.endsWith(".jpg") || str.endsWith(".mp4") || str.endsWith(".gif")) {
                        HelperMethods helperMethods = new HelperMethods(this.context);
                        HelperMethods.transfer(file);
                    }
                }
                return new Boolean(true);
            } catch (Exception e) {
                e.printStackTrace();
                return new Boolean(false);
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            Builder builder;
            PendingIntent activity;
            if (bool == null) {
                Log.d("", "Boolean was Null... Strange.");
            }
            if (bool.booleanValue()) {
                this.notifManager.cancel(420);
                try {
                    activity = PendingIntent.getActivity(this.context, 4896, new Intent(this.context, Class.forName("com.whatsappstatus.saver.MainActivity")), 0);
                    builder = new Builder(this.context);
                    builder.setContentText("All Statuses Saved! ✓").setContentIntent(activity).setColor(ContextCompat.getColor(this.context, R.color.colorAccent)).setSmallIcon(R.drawable.notif);
                    this.notifManager.notify(4896, builder.build());
                } catch (Throwable e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
            if (!bool.booleanValue()) {
                this.notifManager.cancel(420);
                try {
                    activity = PendingIntent.getActivity(this.context, 4896, new Intent(this.context, Class.forName("com.whatsappstatus.saver.MainActivity")), 0);
                    builder = new Builder(this.context);
                    builder.setContentText("There was a problem Saving All of the Statuses!").setContentIntent(activity).setColor(ContextCompat.getColor(this.context, R.color.colorAccent)).setSmallIcon(17301543);
                    this.notifManager.notify(4896, builder.build());
                } catch (Throwable e2) {
                    throw new NoClassDefFoundError(e2.getMessage());
                }
            }
            super.onPostExecute(bool);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String stringExtra = intent.getStringExtra("actionMode");
        if (stringExtra.equals("saveAll")) {
            new TransferAll(this, context, notificationManager).execute(new Void[0]);
        }
        if (stringExtra.equals("openApp")) {
            notificationManager.cancel(0);
            notificationManager.cancelAll();
            closePanel(context);
            try {
                Intent intent2 = new Intent(context, Class.forName("com.whatsappstatus.saver.MainActivity"));
//                intent2.addFlags(335577088);
                context.startActivity(intent2);
            } catch (Throwable e) {
                throw new NoClassDefFoundError(e.getMessage());
            }
        }
    }

    public void closePanel(Context context) {
        context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }
}
