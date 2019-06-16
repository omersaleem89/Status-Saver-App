package com.whatsappstatus.saver;


import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * Created by umer on 23-Apr-18.
 */

public class HelperMethods {

    private static Context classContext;


    public HelperMethods(Context context) {
        classContext = context;
    }

    public static void transfer(File file) {
        try {
            String stringBuffer = Environment.getExternalStorageDirectory().getAbsolutePath() + "/StorySaver/";
            copyFile(file, new File(stringBuffer + file.getName()));
            if (VERSION.SDK_INT >= 19) {
                MediaScannerConnection.scanFile(classContext, new String[]{stringBuffer + file.getName()}, (String[]) null, (OnScanCompletedListener) null);
                return;
            }
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(Uri.fromFile(new File(stringBuffer + file.getName())));
            classContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Status Saver", " " + e.getMessage());

        }
    }

    public static void copyFile(File file, File file2) throws IOException {
        Log.e("Copy", "error");
        Throwable th;
        Throwable th2;
        if (!file2.getParentFile().exists()) {
            file2.getParentFile().mkdirs();
        }
        if (!file2.exists()) {
            file2.createNewFile();
        }
        FileChannel fileChannel = (FileChannel) null;
        FileChannel fileChannel2 = (FileChannel) null;
        FileChannel channel;
        try {
            channel = new FileInputStream(file).getChannel();
            try {
                fileChannel = new FileOutputStream(file2).getChannel();
            } catch (Throwable th3) {
                th = th3;
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                throw th;
            }
            try {
                fileChannel.transferFrom(channel, (long) 0, channel.size());
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
            } catch (Throwable th4) {
                th2 = th4;
                fileChannel2 = fileChannel;
                th = th2;
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                throw th;
            }
        } catch (Throwable th5) {
            th2 = th5;
            channel = fileChannel;
            th = th2;
            if (channel != null) {
                channel.close();
            }
            if (fileChannel2 != null) {
                fileChannel2.close();
            }
            try {
                throw th;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    public static File getLatestFilefromDir(String str) {
        File[] listFiles = new File(str).listFiles();
        if (listFiles == null || listFiles.length == 0) {
            return (File) null;
        }
        File file = listFiles[0];
        for (int i = 1; i < listFiles.length; i++) {
            if (file.lastModified() < listFiles[i].lastModified()) {
                file = listFiles[i];
            }
        }
        return file;
    }

}