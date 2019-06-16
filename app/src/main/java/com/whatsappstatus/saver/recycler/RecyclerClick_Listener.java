package com.whatsappstatus.saver.recycler;

import android.view.View;

/**
 * Created by SONU on 15/03/16.
 */
public interface RecyclerClick_Listener {

    /**
     * Interface for Recycler View Click listener
     **/

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}