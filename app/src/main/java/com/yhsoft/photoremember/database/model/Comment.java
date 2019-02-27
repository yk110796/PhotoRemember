package com.yhsoft.photoremember.database.model;

public class Comment {
    public static final String ID = "id";
    public static final String CONTENTS_ID = "contents_id";
    public static final String MEMBER_ID = "member_id";
    public static final String TARGET_MEMBER = "target_member";
    public static final String COMMENTS = "comments";
    public static final String DATE = "date";

    private int id;
    private int contents_id;
    private int member_id;
    private int target_member;
    private String comments;
    private int date;

    public void setID(int id) {
        this.id = id;
    }

    public void setContentId(int id) {
        contents_id = id;
    }

    public void setMemberId(int id) {
        member_id = id;
    }

    public void setTargetMember(int id) {
        target_member = id;
    }

    public void setComment(String str) {
        comments = str;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getID() {
        return id;
    }

    public int getContentId() {
        return contents_id;
    }

    public int getMemberId() {
        return member_id;
    }

    public int getTargetMember() {
        return target_member;
    }

    public String getComment() {
        return comments;
    }

    public int getDate() {
        return date;
    }

}
