package com.yhsoft.photoremember.database.model;

/**
 * Contents Model
 * This class includes
 */
public class Contents {
    public static final String ID = "id";
    public static final String CATEGORY = "category";
    public static final String EXPOSE_LEVEL = "expose_level";
    public static final String REG_DATE = "reg_date";
    public static final String MOD_DATE = "mod_date";
    public static final String MEMBER_ID = "member_id";
    public static final String MAIN_PIC_ID = "main_pic_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String THEME_CATEGORY = "theme_category";

    private int id;
    private int category;
    private int expose_level;
    private int reg_date;
    private int mod_date;
    private int member_id;
    private int main_pic_id;
    private String title;
    private String description;
    private int theme_category;

    public void setID(int id) {
        this.id = id;
    }

    public void setCategory(int cat) {
        category = cat;
    }

    public void setLevel(int value) {
        expose_level = value;
    }

    public void setRegDate(int date) {
        reg_date = date;
    }

    public void setModDate(int date) {
        mod_date = date;
    }

    public void setRegisterID(int id) {
        member_id = id;
    }

    public void setMainPic(int id) {
        main_pic_id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public void setThemeCategory(int category) {
        theme_category = category;
    }

    public int getID() {
        return id;
    }

    public int getCategory() {
        return category;
    }

    public int getLevel() {
        return expose_level;
    }

    public int getRegDate() {
        return reg_date;
    }

    public int getModDate() {
        return mod_date;
    }

    public int getRegisterID() {
        return member_id;
    }

    public int getMainPicID() {
        return main_pic_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getThemeCategory() {
        return theme_category;
    }

}
