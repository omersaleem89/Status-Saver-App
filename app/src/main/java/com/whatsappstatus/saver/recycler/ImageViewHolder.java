package com.whatsappstatus.saver.recycler;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatsappstatus.saver.R;

/**
 * Created by SONU on 27/03/16.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {


    public ImageView imageView,imageViewCheck;


    public ImageViewHolder(View view) {
        super(view);


        this.imageView = (ImageView) view.findViewById(R.id.imageView_wa_image);
        this.imageViewCheck = (ImageView) view.findViewById(R.id.imageView_wa_checked);

    }
}