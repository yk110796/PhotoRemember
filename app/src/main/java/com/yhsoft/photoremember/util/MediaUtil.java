package com.yhsoft.photoremember.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.view.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import static android.graphics.BitmapFactory.Options;
import static android.graphics.BitmapFactory.decodeFile;
import static android.net.Uri.withAppendedPath;
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Images.ImageColumns;
import static android.provider.MediaStore.Images.Media;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Images.Media.getBitmap;
import static android.provider.MediaStore.Images.Thumbnails;
import static android.provider.MediaStore.Images.Thumbnails.DATA;
import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;
import static android.provider.MediaStore.Images.Thumbnails.queryMiniThumbnail;
import static com.bumptech.glide.Glide.with;
import static com.yhsoft.photoremember.PhoTrace.getContext;

public class MediaUtil {


    public static void setScaledImage(ImageView view, int id) {
        with(getContext())
                .loadFromMediaStore(
                        withAppendedPath(EXTERNAL_CONTENT_URI, Integer.toString(id)))
                .fitCenter()
                .override(100, 100)
                .into(view);
    }

    public static void setFitTouchImage(TouchImageView view, int id) {
        Picasso.with(getContext())
                .load(withAppendedPath(EXTERNAL_CONTENT_URI, Integer.toString(id)))
                .placeholder(view.getDrawable())
                .into(view);

    }

    public static void setFitImage(ImageView view, int id) {
        Picasso.with(getContext())
                .load(withAppendedPath(EXTERNAL_CONTENT_URI, Integer.toString(id)))
                .placeholder(view.getDrawable())
                .into(view);

    }

    public static void setFitImageWithCompletedCallBack(ImageView view, int id, Callback callback) {
        Picasso.with(getContext())
                .load(withAppendedPath(EXTERNAL_CONTENT_URI, Integer.toString(id)))
                .placeholder(view.getDrawable())
                .into(view, callback);
    }

    public static void setThumbnail(ImageView view, int id) {
        Picasso.with(getContext())
                .load(withAppendedPath(EXTERNAL_CONTENT_URI, Integer.toString(id)))
                .resize(100, 100)
                .centerCrop()
                .into(view);
    }


    public static Bitmap getThumbnail(int id) {
        String path = null;
        Context context = getContext();
        Bitmap bitmap;
        String[] proj = {DATA};
        bitmap = Thumbnails.getThumbnail(context.getContentResolver(), id, MICRO_KIND, null);
        if (bitmap == null) {
            Cursor mini = queryMiniThumbnail(context.getContentResolver(), id, MINI_KIND, proj);
            if (mini != null && mini.moveToFirst()) {
                path = mini.getString(mini.getColumnIndex(proj[0]));
            }
            Options options = new Options();
            options.inJustDecodeBounds = true;
            decodeFile(path, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            if (options.outWidth > 96) {

                int ws = options.outWidth / 96 + 1;
                if (ws > options.inSampleSize)
                    options.inSampleSize = ws;
            }
            if (options.outHeight > 96) {

                int hs = options.outHeight / 96 + 1;
                if (hs > options.inSampleSize)
                    options.inSampleSize = hs;
            }
            bitmap = decodeFile(path, options);
        }
        return bitmap;
    }

    public static Bitmap getImageBitmap(int id) {
        String path = null;
        Context context = getContext();
        Bitmap bitmap;
        String[] proj = {Media.DATA};
        try {
            bitmap = getBitmap(context.getContentResolver(),
                    withAppendedPath(EXTERNAL_CONTENT_URI, Integer.toString(id)));
        } catch (IOException e) {
            e.printStackTrace();
//        }
//        if( bitmap == null ) {
//                Cursor mini = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, proj);
//                if( mini != null && mini.moveToFirst() ) {
//                    path = mini.getString(mini.getColumnIndex(proj[0]));
//                }
            Options options = new Options();
            //options.inJustDecodeBounds = true;
            //BitmapFactory.decodeFile(path, options);
            //options.inJustDecodeBounds = false;
            //options.inSampleSize = 1;

            bitmap = decodeFile(path, options);
        }
        return bitmap;
    }

    public static ArrayList<String> getFilePaths() {
        Uri u = EXTERNAL_CONTENT_URI;
        String[] projection = {ImageColumns.DATA};
        Cursor c = null;
        Context context = getContext();
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();
        String[] directories = null;
        int fileNum = 0;
        if (u != null) {
            c = context.getContentResolver().query(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();

                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                    ) {
                        String path = imagePath.getAbsolutePath();
                        resultIAV.add(path);
                        fileNum++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return resultIAV;
    }

    public static int getFileNum() {
        Uri u = EXTERNAL_CONTENT_URI;
        String[] projection = {ImageColumns.DATA};
        Cursor c = null;
        Context context = getContext();
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();
        String[] directories = null;
        int fileNum = 0;
        if (u != null) {
            c = context.getContentResolver().query(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();

                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                    ) {
                        String path = imagePath.getAbsolutePath();
                        resultIAV.add(path);
                        fileNum++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return fileNum;
    }


    public static void getBigThumbnail(ImageView view, int id) {
        with(getContext())
                .loadFromMediaStore(
                        withAppendedPath(EXTERNAL_CONTENT_URI, Integer.toString(id)))
                .fitCenter()
                .crossFade()
                .into(view);
    }

}
