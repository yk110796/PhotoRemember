/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.database.model;

public class Friend {
    public static final String ID = "id";
    public static final String NAME = "name";

    private int friendId;
    private String friendName;

    public void setID(int id) {
        friendId = id;
    }

    public void setName(String name) {
        friendName = name;
    }

    public int getID() {
        return friendId;
    }

    public String getName() {
        return friendName;
    }

}
