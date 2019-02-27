/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.ui;

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
