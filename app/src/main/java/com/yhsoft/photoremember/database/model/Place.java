package com.yhsoft.photoremember.database.model;

public class Place {
    public static final String ID = "id";
    public static final String NAME = "name";
    //    public static final String CAT1 = "category_a";
//    public static final String CAT2 = "category_b";
//    public static final String CAT3 = "category_c";
    public static final String LAT = "latitude";
    public static final String LNG = "longitude";
    //    public static final String MEMBER_ID = "member_id";
    public static final String ADDRESS = "address";
//    public static final String SHORT_BRIEF = "brief";
//    public static final String POINT = "point";
//    public static final String INTRO = "introduction";
//    public static final String MENU = "menu";
//    public static final String REG_DATE = "reg_date";
//    public static final String MOD_DATE = "mod_date";
//    public static final String PAID = "paid";
//    public static final String TRANSPORT = "transport";
//    public static final String OWNER_ID = "owner_id";

    private int id;
    private String name;
    //    private int category1;
//    private int category2;
//    private int category3;
    private double latitude;
    private double longitude;
    //    private int member_id;
    private String address;
//    private String brief;
//    private int point;
//    private String introduction;
//    private String menu;
//    private int reg_date;
//    private int mod_date;
//    private int paid;
//    private String transport;
//    private int owner_id;

    private boolean bIsRegistered;

    public void setID(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public void setCategory1(int cat) {
//        category1 = cat;
//    }

//    public void setCategory2(int cat) {
//        category2 = cat;
//    }

//    public void setCategory3(int cat) {
//        category3 = cat;
//    }

    public void setLat(double lat) {
        latitude = lat;
    }

    public void setLng(double lng) {
        longitude = lng;
    }

//    public void setMember(int id) {
//        member_id = id;
//    }

    public void setAddress(String addr) {
        address = addr;
    }

//    public void setBrief(String str) {
//        brief = str;
//    }
//
//    public void setPoint(int point) {
//        this.point = point;
//    }
//
//    public void setIntroduction(String intro) {
//        introduction = intro;
//    }
//
//    public void setMenu(String str) {
//        menu = str;
//    }
//
//    public void setRegDate(int date) {
//        reg_date = date;
//    }
//
//    public void setModDate(int date) {
//        mod_date = date;
//    }
//
//    public void setPaid(int p) {
//        paid = p;
//    }
//
//    public void setTransport(String trans) {
//        transport = trans;
//    }
//
//    public void setOwner(int id) {
//        owner_id = id;
//    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

//    public int getCategory1() {
//        return category1;
//    }
//
//    public int getCategory2() {
//        return category2;
//    }
//
//    public int getCategory3() {
//        return category3;
//    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

//    public int getMemberId() {
//        return member_id;
//    }

    public String getAddress() {
        return address;
    }

//    public String getBrief() {
//        return brief;
//    }
//
//    public int getPoint() {
//        return point;
//    }
//
//    public String getIntroduction() {
//        return introduction;
//    }
//
//    public String getMenu() {
//        return menu;
//    }
//
//    public int getRegDate() {
//        return reg_date;
//    }
//
//    public int getModDate() {
//        return mod_date;
//    }
//
//    public int getPaid() {
//        return paid;
//    }
//
//    public String getTransport() {
//        return transport;
//    }
//
//    public int getOwner() {
//        return owner_id;
//    }
}
