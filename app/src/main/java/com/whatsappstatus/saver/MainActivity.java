package com.whatsappstatus.saver;


import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.whatsappstatus.saver.fragments.bwa.BWAFragment;
import com.whatsappstatus.saver.fragments.wa.WAFragment;
import java.io.File;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10
                .setRemindInterval(10) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);


        try {
            if (!isMyServiceRunning(Class.forName("com.whatsappstatus.saver.service.NotificationService"))) {
                try {
                    startService(new Intent(this, Class.forName("com.whatsappstatus.saver.service.NotificationService")));
                } catch (Throwable e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stash();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            Fragment fragment = new WAFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.framelayout, fragment).commit();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "Share WApp Status Saver App");
            intent.putExtra("android.intent.extra.TEXT", "Try this Awesome App 'WhatsApp Status Saver' which helps you in Saving all the WhatsApp Statuses ..! \nhttps://play.google.com/store/apps/details?id=com.bestwhatsappstatus.saver");
            startActivity(Intent.createChooser(intent, "Share Via"));
            return true;
        }
        if (id == R.id.action_help){
            View inflate = LayoutInflater.from(this).inflate(R.layout.tut, (ViewGroup) null);
            Builder builder = new Builder(this);
            builder.setView(inflate).setTitle("How To Use?").setPositiveButton("Ok!", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_whatsapp) {
                Fragment fragment = new WAFragment();
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.framelayout, fragment).commit();
        }
            // Handle the camera action
         else if (id == R.id.nav_business) {
            if(checkInstallation("com.whatsapp.w4b")) {
                Fragment fragment = new BWAFragment();
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.framelayout, fragment).commit();
            }
            else{
                Toast.makeText(this, "Business Whatsapp Not Installed", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            AppRate.with(this).showRateDialog(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void stash() {
        File file = new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("WhatsApp/Media/.Statuses").toString());
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("WhatsApp Business/Media/.Statuses").toString());
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("GBWhatsApp/Media/.Statuses").toString());
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("StorySaver/").toString()).mkdirs();
    }
    private boolean checkInstallation(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private boolean isMyServiceRunning(Class<?> cls) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

