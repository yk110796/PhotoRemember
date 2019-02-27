package com.yhsoft.photoremember.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.database.model.Comment;
import com.yhsoft.photoremember.database.model.Contents;
import com.yhsoft.photoremember.database.model.ContentsPhoto;
import com.yhsoft.photoremember.database.model.Friend;
import com.yhsoft.photoremember.database.model.FriendPhoto;
import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.database.model.Place;
import com.yhsoft.photoremember.util.DebugLog;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.CursorFactory;
import static com.yhsoft.photoremember.PhoTrace.getContext;
import static com.yhsoft.photoremember.database.model.Comment.COMMENTS;
import static com.yhsoft.photoremember.database.model.Comment.CONTENTS_ID;
import static com.yhsoft.photoremember.database.model.Comment.TARGET_MEMBER;
import static com.yhsoft.photoremember.database.model.Contents.CATEGORY;
import static com.yhsoft.photoremember.database.model.Contents.EXPOSE_LEVEL;
import static com.yhsoft.photoremember.database.model.Contents.MAIN_PIC_ID;
import static com.yhsoft.photoremember.database.model.Contents.MEMBER_ID;
import static com.yhsoft.photoremember.database.model.Contents.MOD_DATE;
import static com.yhsoft.photoremember.database.model.Contents.REG_DATE;
import static com.yhsoft.photoremember.database.model.Contents.THEME_CATEGORY;
import static com.yhsoft.photoremember.database.model.ContentsPhoto.CONTENT_ID;
import static com.yhsoft.photoremember.database.model.FriendPhoto.FRIEND_ID;
import static com.yhsoft.photoremember.database.model.FriendPhoto.PHOTO_ID;
import static com.yhsoft.photoremember.database.model.Photo.DATE;
import static com.yhsoft.photoremember.database.model.Photo.DESCRIPTION;
import static com.yhsoft.photoremember.database.model.Photo.ID;
import static com.yhsoft.photoremember.database.model.Photo.LAT;
import static com.yhsoft.photoremember.database.model.Photo.LNG;
import static com.yhsoft.photoremember.database.model.Photo.PLACE;
import static com.yhsoft.photoremember.database.model.Photo.TITLE;
import static com.yhsoft.photoremember.database.model.Place.ADDRESS;
import static com.yhsoft.photoremember.database.model.Place.NAME;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static java.lang.String.valueOf;

public class DataBaseHelper {
    private static final String DATABASE_NAME = "photrace.db";
    public static final String TABLE_NAME_PHOTO = "photo_table";
    public static final String TABLE_NAME_CONTENTS = "contents_table";
    public static final String TABLE_NAME_COMMENT = "comment_table";
    public static final String TABLE_NAME_PLACE = "place_table";
    public static final String TABLE_NAME_FRIEND = "friend_table";
    public static final String TABLE_NAME_FRIEND_PHOTO = "friend_photo_table";
    public static final String TABLE_NAME_CONTENTS_PHOTO = "contents_photo_table";

    public static final String COMMON_ID = "_idx";

    public static final int DATABASE_VERSION = 2;

    private static final String CREATE_TABLE_PHOTO =
            "CREATE TABLE " + TABLE_NAME_PHOTO + " ("
                    + COMMON_ID + " INTEGER PRIMARY KEY, "
                    + ID + " INTEGER NOT NULL, "
//                    + Photo.HASH + " INTEGER, "
//                    + Photo.MEMBER_ID + " INTEGER, "
                    + DATE + " INTEGER, "
                    + LAT + " REAL, "
                    + LNG + " REAL, "
                    + TITLE + " TEXT, "
                    + DESCRIPTION + " TEXT, "
//                    + Photo.EXPOSE_LEVEL + " INTEGER, "
                    + PLACE + " INTEGER)";

    private static final String CREATE_TABLE_PLACE =
            "CREATE TABLE " + TABLE_NAME_PLACE + " ("
                    + COMMON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Place.ID + " INTEGER, "
                    + NAME + " TEXT, "
//                    + Place.CAT1 + " INTEGER, "
//                    + Place.CAT2 + " INTEGER, "
//                    + Place.CAT3 + " INTEGER, "
                    + Place.LAT + " REAL, "
                    + Place.LNG + " REAL, "
//                    + Place.MEMBER_ID + " INTEGER, "
                    + ADDRESS + " TEXT)";
//                    + Place.SHORT_BRIEF + " TEXT, "
//                    + Place.POINT + " INTEGER, "
//                    + Place.INTRO + " TEXT, "
//                    + Place.MENU + " TEXT, "
//                    + Place.REG_DATE + " INTEGER, "
//                    + Place.MOD_DATE + " INTEGER, "
//                    + Place.PAID + " INTEGER, "
//                    + Place.TRANSPORT + " TEXT, "
//                    + Place.OWNER_ID + " INTEGER)";

    private static final String CREATE_TABLE_FRIEND =
            "CREATE TABLE " + TABLE_NAME_FRIEND + " ("
                    + COMMON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Friend.ID + " INTEGER, "
                    + Friend.NAME + " TEXT)";

    private static final String CREATE_TABLE_CONTENTS =
            "CREATE TABLE " + TABLE_NAME_CONTENTS + " ("
                    + COMMON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Contents.ID + " INTEGER, "
                    + CATEGORY + " INTEGER, "
                    + EXPOSE_LEVEL + " INTEGER, "
                    + REG_DATE + " INTEGER, "
                    + MOD_DATE + " INTEGER, "
                    + MEMBER_ID + " INTEGER, "
                    + MAIN_PIC_ID + " INTEGER, "
                    + Contents.TITLE + " TEXT, "
                    + Contents.DESCRIPTION + " TEXT, "
                    + THEME_CATEGORY + " INTEGER)";

    private static final String CREATE_TABLE_COMMENT =
            "CREATE TABLE " + TABLE_NAME_COMMENT + " ("
                    + COMMON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Comment.ID + " INTEGER, "
                    + CONTENTS_ID + " INTEGER, "
                    + Comment.MEMBER_ID + " INTEGER, "
                    + TARGET_MEMBER + " INTEGER, "
                    + COMMENTS + " TEXT, "
                    + Comment.DATE + " INTEGER)";

    private static final String CREATE_TABLE_FRIEND_PHOTO =
            "CREATE TABLE " + TABLE_NAME_FRIEND_PHOTO + " ("
                    + COMMON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FRIEND_ID + " INTEGER, "
                    + PHOTO_ID + " INTEGER)";

    private static final String CREATE_TABLE_CONTENTS_PHOTO =
            "CREATE TABLE " + TABLE_NAME_CONTENTS_PHOTO + " ("
                    + COMMON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CONTENT_ID + " INTEGER, "
                    + ContentsPhoto.PHOTO_ID + " INTEGER)";

    private DbOpenHelper mDbOpener;
    private SQLiteDatabase mDatabase;
    private DataBaseReadyListener mListener = null;

    public interface DataBaseReadyListener {
        public void onDbOpenComplete();
    }

    /**
     * Static Factory Initializer
     * - Provide singleton instance of DataBaseHelper which is referenced from anywhere
     */
    private static DataBaseHelper mInstance = null;

    public static DataBaseHelper getInstance() {
        if (mInstance == null) {
            mInstance = new DataBaseHelper();
        }
        return mInstance;
    }

    public DbOpenHelper getmDbOpener() {
        return mDbOpener;
    }

    public SQLiteDatabase getmDatabase() {
        return mDatabase;
    }

    public void initDataBase() {
        PhoTrace app = (PhoTrace) getContext().getApplicationContext();
        //mDbOpener = new DbOpenHelper(PhoTrace.getContext());
        mDbOpener = new DbOpenHelper(getContext(), app.db_path + "/photrace.db", null, DATABASE_VERSION);
        mDatabase = mDbOpener.getWritableDatabase();
        mDbOpener.refreshOnStartUp(mDatabase);
        //
    }

    public void closeDataBase() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }

    public class DbOpenHelper extends SQLiteOpenHelper {
        public DbOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DbOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            e("DbOpenHelper onCreate");
            database.execSQL(CREATE_TABLE_PHOTO);
            database.execSQL(CREATE_TABLE_PLACE);
            database.execSQL(CREATE_TABLE_FRIEND);
            database.execSQL(CREATE_TABLE_CONTENTS);
            database.execSQL(CREATE_TABLE_COMMENT);
            database.execSQL(CREATE_TABLE_FRIEND_PHOTO);
            database.execSQL(CREATE_TABLE_CONTENTS_PHOTO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int i, int i2) {
            // Do nothing because we always refresh the database at the start
        }

        /**
         * TODO Refresh on startup
         * need to search and update using Media ID
         *
         * @param database
         */
        public void refreshOnStartUp(SQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PHOTO);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PLACE);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FRIEND);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTENTS);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COMMENT);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FRIEND_PHOTO);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTENTS_PHOTO);

            onCreate(database);
        }
    }

    public void beginTransaction() {
        mDatabase.beginTransaction();
    }

    public void setTransactionSuccess() {
        mDatabase.setTransactionSuccessful();
    }

    public void endTransaction() {
        mDatabase.endTransaction();
    }

    public long insertData(ContentValues data, String TableName) {
        return mDatabase.insert(TableName, null, data);
    }

    public long updateData(ContentValues data, String tableName, String whereClause) {
        return mDatabase.update(tableName, data, whereClause, null);
//        return mDatabase.update(tableName, data, whereClause, new String[]{String.valueOf(whereID)});
    }

    public void removeData(String tableName, String whereClause, int whereId) {
        mDatabase.delete(tableName, whereClause, new String[]{valueOf(whereId)});
    }

    public void removeAllData(String tableName) {
        mDatabase.execSQL("delete from " + tableName);
    }


    /**
     * Select queries
     */
    public static final String SELECT_PHOTO_ALL =
            "select * from " + TABLE_NAME_PHOTO + " order by " + DATE + " desc";

    public static final String SELECT_PHOTO_WITH_EMPTY =
            "select * from " + TABLE_NAME_PHOTO + " where " + TITLE + " is null order by " + DATE + " desc";

    public static final String SELECT_PLACE_ALL =
            "select * from " + TABLE_NAME_PLACE + " order by " + NAME + " desc";

    public static final String SELECT_THEME_ALL =
            "select * from " + TABLE_NAME_CONTENTS + " order by " + THEME_CATEGORY + " desc";

    public static final String SELECT_THEME_WITH_CATEGORY =
            "select * from " + TABLE_NAME_CONTENTS + " where " + THEME_CATEGORY + " is ";


    public String getQueryRange(long start, long end) {
        return "select * from " + TABLE_NAME_PHOTO + " where "
                + DATE + " >= " + start
                + " or " + DATE + " <= " + end + " order by " + DATE + " desc";
    }

    public Photo selectPhoto(int id) {
        Photo item = null;
        String sql = "select * from " + TABLE_NAME_PHOTO + " where " + ID + " = " + id;
        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            item = new Photo();
            item.setID(cursor.getInt(cursor.getColumnIndex(ID)));
//            item.setHash(cursor.getInt(cursor.getColumnIndex(Photo.HASH)));
//            item.setOwner(cursor.getInt(cursor.getColumnIndex(Photo.MEMBER_ID)));
            item.setDate(cursor.getLong(cursor.getColumnIndex(DATE)));
            item.setLatitude(cursor.getDouble(cursor.getColumnIndex(LAT)));
            item.setLongitude(cursor.getDouble(cursor.getColumnIndex(LNG)));
            item.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
            item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
//            item.setExpose_level(cursor.getInt(cursor.getColumnIndex(Photo.EXPOSE_LEVEL)));
            item.setPlace(cursor.getInt(cursor.getColumnIndex(PLACE)));

            cursor.close();
        }
        return item;
    }

    public boolean checkIfNoPhoto(int id) {
        String sql = "select * from " + TABLE_NAME_PHOTO + " where " + ID + " = " + id;
        Cursor cursor = mDatabase.rawQuery(sql, null);
        try {
            if (cursor.getCount() <= 0) {
                return true;
            }
            return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }

    }

    public int getPlaceTableCount() {
        String sql = "select * from " + TABLE_NAME_PLACE;
        Cursor cursor = mDatabase.rawQuery(sql, null);
        return cursor.getCount();
    }

    public ArrayList<Photo> collectPhotoWithQuery(String sql) {
        ArrayList<Photo> photos = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Photo item = new Photo();
                item.setID(cursor.getInt(cursor.getColumnIndex(ID)));
//                item.setHash(cursor.getInt(cursor.getColumnIndex(Photo.HASH)));
//                item.setOwner(cursor.getInt(cursor.getColumnIndex(Photo.MEMBER_ID)));
                item.setDate(cursor.getLong(cursor.getColumnIndex(DATE)));
                item.setLatitude(cursor.getDouble(cursor.getColumnIndex(LAT)));
                item.setLongitude(cursor.getDouble(cursor.getColumnIndex(LNG)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
//                item.setExpose_level(cursor.getInt(cursor.getColumnIndex(Photo.EXPOSE_LEVEL)));
                item.setPlace(cursor.getInt(cursor.getColumnIndex(PLACE)));

                photos.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return photos;
        }
        return null;
    }

    public ArrayList<Integer> collectPhotoIDwithQuery(String sql) {
        ArrayList<Integer> photos = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                photos.add(cursor.getInt(cursor.getColumnIndex(ID)));
            } while (cursor.moveToNext());
            cursor.close();
            return photos;
        }
        return null;
    }

    public ArrayList<Contents> collectContentsWithQuery(String sql) {
        ArrayList<Contents> contents = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Contents item = new Contents();
                item.setID(cursor.getInt(cursor.getColumnIndex(Contents.ID)));
                item.setCategory(cursor.getInt(cursor.getColumnIndex(CATEGORY)));
                item.setLevel(cursor.getInt(cursor.getColumnIndex(EXPOSE_LEVEL)));
                item.setRegDate(cursor.getInt(cursor.getColumnIndex(REG_DATE)));
                item.setModDate(cursor.getInt(cursor.getColumnIndex(MOD_DATE)));
                item.setRegisterID(cursor.getInt(cursor.getColumnIndex(MEMBER_ID)));
                item.setMainPic(cursor.getInt(cursor.getColumnIndex(MAIN_PIC_ID)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(Contents.TITLE)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(Contents.DESCRIPTION)));
                item.setThemeCategory(cursor.getInt(cursor.getColumnIndex(THEME_CATEGORY)));

                contents.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return contents;
        }
        return null;
    }

    public ArrayList<Contents> collectThemeWithCategory(int category) {
        ArrayList<Contents> contents = new ArrayList<>();
        String query = SELECT_THEME_WITH_CATEGORY + category + " order by " + Contents.TITLE + " desc";
        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Contents item = new Contents();
                item.setID(cursor.getInt(cursor.getColumnIndex(Contents.ID)));
                item.setCategory(cursor.getInt(cursor.getColumnIndex(CATEGORY)));
                item.setLevel(cursor.getInt(cursor.getColumnIndex(EXPOSE_LEVEL)));
                item.setRegDate(cursor.getInt(cursor.getColumnIndex(REG_DATE)));
                item.setModDate(cursor.getInt(cursor.getColumnIndex(MOD_DATE)));
                item.setRegisterID(cursor.getInt(cursor.getColumnIndex(MEMBER_ID)));
                item.setMainPic(cursor.getInt(cursor.getColumnIndex(MAIN_PIC_ID)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(Contents.TITLE)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(Contents.DESCRIPTION)));
                item.setThemeCategory(cursor.getInt(cursor.getColumnIndex(THEME_CATEGORY)));

                contents.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return contents;
        }
        return null;
    }

    public ArrayList<Place> collectPlaceWithQuery(String sql) {
        ArrayList<Place> places = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Place item = new Place();
                item.setID(cursor.getInt(cursor.getColumnIndex(Place.ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
//                item.setCategory1(cursor.getInt(cursor.getColumnIndex(Place.CAT1)));
//                item.setCategory2(cursor.getInt(cursor.getColumnIndex(Place.CAT2)));
//                item.setCategory3(cursor.getInt(cursor.getColumnIndex(Place.CAT3)));
                item.setLat(cursor.getDouble(cursor.getColumnIndex(Place.LAT)));
                item.setLng(cursor.getDouble(cursor.getColumnIndex(Place.LNG)));
//                item.setMember(cursor.getInt(cursor.getColumnIndex(Place.MEMBER_ID)));
                item.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)));
//                item.setBrief(cursor.getString(cursor.getColumnIndex(Place.SHORT_BRIEF)));
//                item.setPoint(cursor.getInt(cursor.getColumnIndex(Place.POINT)));
//                item.setIntroduction(cursor.getString(cursor.getColumnIndex(Place.INTRO)));
//                item.setMenu(cursor.getString(cursor.getColumnIndex(Place.MENU)));
//                item.setRegDate(cursor.getInt(cursor.getColumnIndex(Place.REG_DATE)));
//                item.setModDate(cursor.getInt(cursor.getColumnIndex(Place.MOD_DATE)));
//                item.setPaid(cursor.getInt(cursor.getColumnIndex(Place.PAID)));
//                item.setTransport(cursor.getString(cursor.getColumnIndex(Place.TRANSPORT)));
//                item.setOwner(cursor.getInt(cursor.getColumnIndex(Place.OWNER_ID)));

                places.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return places;
        }
        return null;
    }

    public Place selectPlace(int id) {
        Place item = null;
        String sql = "select * from " + TABLE_NAME_PLACE + " where " + Place.ID + " = " + id;
        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            item = new Place();
            item.setID(cursor.getInt(cursor.getColumnIndex(Place.ID)));
            item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            cursor.close();
        }
        return item;
    }

    public ArrayList<Long> selectPhotoDate() {
        ArrayList<Long> date = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME_PHOTO;

        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                date.add(cursor.getLong(cursor.getColumnIndex(DATE)));
            } while (cursor.moveToNext());
            cursor.close();
            return date;
        }
        return null;
    }

    public int selectPhotoCount() {
        int count = 0;
        String sql = "select * from " + TABLE_NAME_PHOTO;

        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());
            cursor.close();
            return count;
        }

        return 0;
    }


    public ArrayList<Place> selectPlaceAll() {
        ArrayList<Place> data = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME_PLACE;

        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Place item = new Place();
                item.setID(cursor.getInt(cursor.getColumnIndex(Place.ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(NAME)));

                e("Item : " + item.getID());
                data.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return data;
        }
        return null;
    }

    public ArrayList<Friend> selectFriendAll() {
        ArrayList<Friend> data = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME_FRIEND;

        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Friend item = new Friend();
                item.setID(cursor.getInt(cursor.getColumnIndex(Place.ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(NAME)));

                e("Item : " + item.getID());
                data.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return data;
        }
        return null;
    }

    public ArrayList<Contents> selectContentsAll() {
        ArrayList<Contents> data = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME_CONTENTS;

        Cursor cursor = mDatabase.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Contents item = new Contents();
                item.setID(cursor.getInt(cursor.getColumnIndex(Place.ID)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(NAME)));

                e("Item : " + item.getID());
                data.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return data;
        }
        return null;
    }
}

