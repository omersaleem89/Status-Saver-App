package com.whatsappstatus.saver.fragments.bwa;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.whatsappstatus.saver.GenericAdapter;
import com.whatsappstatus.saver.HelperMethods;
import com.whatsappstatus.saver.InstanceHandler;
import com.whatsappstatus.saver.R;
import com.whatsappstatus.saver.adapter.WAVideoAdapter;
import com.whatsappstatus.saver.model.WAImageModel;
import com.whatsappstatus.saver.recycler.RecyclerClick_Listener;
import com.whatsappstatus.saver.recycler.RecyclerTouchListener;
import com.whatsappstatus.saver.recycler.ToolbarActionModeCallback;
import com.whatsappstatus.saver.viewer.VideoPlayer;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BWAVideoFragment extends Fragment {
    private static BWAVideoFragment mInstance;
    RecyclerView recyclerView;
    FragmentActivity activity;
    ProgressBar progressBar;
    FloatingActionButton fab;
    WAVideoAdapter waVideoAdapter;
    ArrayList<WAImageModel> arrayList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    private static View v;
    private ActionMode mActionMode;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    public BWAVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_wa_video, container, false);
        activity = getActivity();
        mInstance = this;
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.ref_wa_video);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_wa_video);
        progressBar = (ProgressBar) v.findViewById(R.id.progressbar_wa_video);

        populateRecyclerView();
        implementRecyclerViewClickListeners();

        MobileAds.initialize(activity, getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        AdView adView = new AdView(activity);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-6699687107380271/2126437250");
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        swipeRefreshLayout.setColorSchemeResources(new int[]{R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimaryDark});
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        fab = (FloatingActionButton) v.findViewById(R.id.wa_video_fab_save_all);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAll();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });
        return v;
    }

    private void populateRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        getStatus();
        waVideoAdapter = new WAVideoAdapter(activity, arrayList);
        recyclerView.setAdapter(waVideoAdapter);
        waVideoAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null)
                    onListItemSelect(position);
                else {
                    String str = waVideoAdapter.getItem(position).getPath();
                    try {
                        Intent intent = new Intent(getActivity(), VideoPlayer.class);
                        intent.putExtra("pos", str);
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 101);
                    } catch (Throwable e) {
                        throw new NoClassDefFoundError(e.getMessage());
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                mActionMode = null;
                onListItemSelect(position);
            }
        }));
    }


    //List item select method
    private void onListItemSelect(int position) {
        waVideoAdapter.toggleSelection(position);//Toggle the selection
        List<Fragment> fragments;

        boolean hasCheckedItems = waVideoAdapter.getSelectedCount() > 0;//Check if any items are already selected or not


        if (hasCheckedItems && mActionMode == null) {
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ToolbarActionModeCallback(getActivity(),new GenericAdapter<WAVideoAdapter>(waVideoAdapter), arrayList,new InstanceHandler<BWAVideoFragment>(mInstance)));
        }
        else if (!hasCheckedItems && mActionMode != null)
        // there no selected items, finish the actionMode
        {
            mActionMode.finish();
            mActionMode=null;
        }

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(waVideoAdapter
                    .getSelectedCount()) + " selected");


    }
    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    private void saveAll() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);

        // set title
        alertDialogBuilder.setTitle("Save All Status");

        // set dialog message
        alertDialogBuilder
                .setMessage("This Action will Save all the available Video Statuses... \nDo you want to Continue?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        File[] listFiles = new File(new StringBuffer().append(Environment.getExternalStorageDirectory().getAbsolutePath()).append("/WhatsApp/Media/.Statuses/").toString()).listFiles();
                        if (waVideoAdapter.getItemCount() == 0) {
                            Toast.makeText(activity, "No Status available to Save...", Toast.LENGTH_SHORT).show();
                        } else {
                            int i = 0;
                            while (i < listFiles.length) {
                                try {
                                    File file = listFiles[i];
                                    String str = file.getName().toString();
                                    if (str.endsWith(".mp4") || str.endsWith(".avi") || str.endsWith(".mkv") || str.endsWith(".gif")) {
                                        HelperMethods helperMethods = new HelperMethods(activity.getApplicationContext());
                                        HelperMethods.transfer(file);
                                    }
                                    i++;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                            Toast.makeText(activity, "Done :)", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black_overlay));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black_overlay));
            }
        });

        // show it
        alertDialog.show();
    }

    public void getStatus(){
        File[] listFiles = new File(new StringBuffer().append(Environment.getExternalStorageDirectory().getAbsolutePath()).append("/WhatsApp Business/Media/.Statuses/").toString()).listFiles();
        if (listFiles != null && listFiles.length >= 1) {
            Arrays.sort(listFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.getName().endsWith(".mp4") || file.getName().endsWith(".avi") || file.getName().endsWith(".mkv") || file.getName().endsWith(".gif")) {
                    WAImageModel model=new WAImageModel(file.getAbsolutePath());
                    arrayList.add(model);
                }
            }
        }
    }
    public void deleteRows() {
        SparseBooleanArray selected = waVideoAdapter
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                arrayList.remove(selected.keyAt(i));
                waVideoAdapter.notifyDataSetChanged();//notify adapter

            }
        }
        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

    }
    public void refresh() {
        if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
        waVideoAdapter.updateData(new ArrayList<WAImageModel>());
        populateRecyclerView();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null)  {
            if(resultCode == -1)
            {
                refresh();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                refresh();
            }
        }
    }

    public InterstitialAd getmInterstitialAd() {
        return mInterstitialAd;
    }
}