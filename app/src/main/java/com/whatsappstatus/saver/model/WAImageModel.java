package com.whatsappstatus.saver.model;

/**
 * Created by umer on 25-Apr-18.
 */

public class WAImageModel {
    private String path;
    private boolean isSelected = false;

    public WAImageModel(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

}
