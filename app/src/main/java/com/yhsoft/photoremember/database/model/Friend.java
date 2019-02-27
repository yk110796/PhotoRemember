package com.yhsoft.photoremember.database.model;

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
