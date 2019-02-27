package com.yhsoft.photoremember.ui;

public class CheckItem {
    public int mID;
    public int mImage;   // or Image URI ?
    public String mName;
    public boolean bIsChecked = false;

    public void setID(int id) {
        mID = id;
    }

    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int id) {
        mImage = id;
    }

    public boolean getChecked() {
        return bIsChecked;
    }

    public void setChecked(boolean value) {
        bIsChecked = value;
    }

}
