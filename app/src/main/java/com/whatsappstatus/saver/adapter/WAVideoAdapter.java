package com.whatsappstatus.saver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.whatsappstatus.saver.recycler.ImageViewHolder;
import com.whatsappstatus.saver.R;
import com.whatsappstatus.saver.model.WAImageModel;
import com.whatsappstatus.saver.recycler.VideoViewHolder;

import java.util.ArrayList;

/**
 * Created by SONU on 27/03/16.
 */
public class WAVideoAdapter extends
        RecyclerView.Adapter<VideoViewHolder> {
    private ArrayList<WAImageModel> arrayList;
    private Context context;
    private SparseBooleanArray mSelectedItemsIds;

    public WAVideoAdapter(Context context,
                          ArrayList<WAImageModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        mSelectedItemsIds = new SparseBooleanArray();

    }


    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);

    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder,
                                 int position) {

        //Setting text over text view
        RequestOptions centerCrop = new RequestOptions().override(holder.imageView.getWidth(), holder.imageView.getHeight()).centerCrop();
        Glide.with(context).asBitmap().apply(centerCrop).load((String) arrayList.get(position).getPath()).transition(BitmapTransitionOptions.withCrossFade()).into(holder.imageView);

        if(mSelectedItemsIds.get(position)) {
            holder.imageViewCheck.setVisibility(View.VISIBLE);
            holder.imageViewPlay.setVisibility(View.GONE);
        }
        else {
            holder.imageViewCheck.setVisibility(View.GONE);
            holder.imageViewPlay.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public VideoViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.wa_video_list_item, viewGroup, false);
        return new VideoViewHolder(mainGroup);

    }


    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public WAImageModel getItem(int i) {
        return (WAImageModel) arrayList.get(i);
    }
    public void updateData(ArrayList<WAImageModel> viewModels) {
        arrayList.clear();
        arrayList.addAll(viewModels);
        notifyDataSetChanged();
    }


}