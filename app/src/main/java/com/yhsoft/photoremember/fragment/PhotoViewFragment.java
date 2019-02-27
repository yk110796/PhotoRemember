package com.yhsoft.photoremember.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.activity.PhotoDetailActivity2;
import com.yhsoft.photoremember.asynctask.ClusteringForMapTask;
import com.yhsoft.photoremember.asynctask.ClusteringTask2;
import com.yhsoft.photoremember.ui.PhotoBucketItem;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.DebugLog;
import com.yhsoft.photoremember.util.MediaUtil;
import com.yhsoft.photoremember.view.ExpandableGridView;
import com.yhsoft.photoremember.view.SquareImageView;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by design on 2015-07-07.
 */
public class PhotoViewFragment extends Fragment {

    Context mContext;
    PhoTrace app;
    Cursor cursor, mapCursor;
    private View rootView = null;
    private final int flag = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;
    private int[] GRID_COLUMN = {3, 6, 10};
    static int gridColumnCount = 0;
    private GridView gridView = null;
    private GridCursorAdapter gAdapter = null;
    String TAG = this.getClass().getSimpleName();
    ArrayList<Integer> photoArray = new ArrayList<Integer>();
    ArrayList<Double> latArray = new ArrayList<Double>();
    ArrayList<Double> lngArray = new ArrayList<Double>();

    ScaleGestureDetector mScaleGestureDetector;
    ClusteringTask2 task;

    PhotoBucketFragment.RecyclerListAdapter mRecyclerListAdapter;
    LinearLayoutManager mLayoutManager;

    int imageNo = -1 ;
    @InjectView(R.id.common_recyclerview)
    RecyclerView mBucketList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = PhoTrace.getContext();
        app = (PhoTrace) mContext.getApplicationContext();

    }


    public void init() { //execute query when moving timeslider
        String leftInMillis = String.valueOf(app.leftInMillis);
        String rightInMillis = String.valueOf(app.rightInMillis);
        app.clearArrayLists();
        initializeCursor(cursor);

        //String selection = " ? <=  " + MediaStore.Images.Media.DATE_TAKEN + " and " + " ? >= " + MediaStore.Images.Media.DATE_TAKEN + " and "
        //        + " ? >= " + MediaStore.Images.Media.LATITUDE + " and  ? >= " + MediaStore.Images.Media.LONGITUDE
        //        + " and ? <= " + MediaStore.Images.Media.LATITUDE + " and ? <= " + MediaStore.Images.Media.LONGITUDE; // +

        String selection2 = " ? <=  " + MediaStore.Images.Media.DATE_TAKEN + " and " + " ? >= " + MediaStore.Images.Media.DATE_TAKEN;
        String sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";


        //   " or " + MediaStore.Images.Media.LATITUDE+" is null and " + MediaStore.Images.Media.LONGITUDE+ " is null" ;

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Thumbnails._ID

        };

        cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection2,
                new String[]{leftInMillis, rightInMillis},
                sortOrder);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    photoArray.add(cursor.getInt(0));
                    latArray.add(cursor.getDouble(cursor.getColumnIndex("latitude")));
                    lngArray.add(cursor.getDouble(cursor.getColumnIndex("longitude")));
                    app.photoArray.add(cursor.getInt(0));
                    app.latArray.add(cursor.getDouble(cursor.getColumnIndex("latitude")));
                    app.lngArray.add(cursor.getDouble(cursor.getColumnIndex("longitude")));
                    app.dateArray.add(cursor.getLong(cursor.getColumnIndex("datetaken")));
                    Log.e(TAG, "dateArray: " + cursor.getLong(cursor.getColumnIndex("datetaken")) );
                } while (cursor.moveToNext());
            }
            Log.e(TAG, "dateArray size: " +  app.dateArray.size() );
        }

        app.photoTotalNum =  photoArray.size();

        new ClusteringTask2(photoArray, latArray, lngArray).execute();
    }

    public void init2() { //execute query when moving map
        String top = String.valueOf(app.top);
        String right = String.valueOf(app.right);
        String bottom = String.valueOf(app.bottom);
        String left = String.valueOf(app.left);
        app.clearArrayLists();
        initializeCursor(mapCursor);
        String selection;


        if (app.top > app.bottom) {// normal
            selection = " ? >= " + MediaStore.Images.Media.LATITUDE + " and  ? <= " + MediaStore.Images.Media.LATITUDE
                    + " and ? >= " + MediaStore.Images.Media.LONGITUDE + " and ? <= " + MediaStore.Images.Media.LONGITUDE;
        } else {//exception
            selection = " ? <= " + MediaStore.Images.Media.LATITUDE + " and  ? >= " + MediaStore.Images.Media.LATITUDE
                    + " and ? <= " + MediaStore.Images.Media.LONGITUDE + " and ? >= " + MediaStore.Images.Media.LONGITUDE;
        }


        String sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Thumbnails._ID

        };

        mapCursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                new String[]{top, bottom, right, left},
                sortOrder);

        app.dateArray.clear();

        /* this code part saves latlng and date for photo's detail view and slide show */
        if (mapCursor != null) {
            if (mapCursor.moveToFirst()) {
                do {
                    photoArray.add(mapCursor.getInt(0));
                    latArray.add(mapCursor.getDouble(mapCursor.getColumnIndex("latitude")));
                    lngArray.add(mapCursor.getDouble(mapCursor.getColumnIndex("longitude")));
                    app.photoArray.add(mapCursor.getInt(0));
                    app.latArray.add(mapCursor.getDouble(mapCursor.getColumnIndex("latitude")));
                    app.lngArray.add(mapCursor.getDouble(mapCursor.getColumnIndex("longitude")));
                    app.dateArray.add(mapCursor.getLong(mapCursor.getColumnIndex("datetaken"))); //여기서 date array add 시킴
                    //구조체 만들어 한묵음으로 만들것
                } while (mapCursor.moveToNext());
            }
           // mapCursor.moveToFirst();
        }

        if(app.dateArray.size() > 0) {
            if (app.dateArray.size() == 1) {
                app.leftInMillis = app.dateArray.get(0);
                app.rightInMillis = app.dateArray.get(0);
            }
            if (app.dateArray.size() > 1) {
                app.leftInMillis = app.dateArray.get(app.dateArray.size() - 1);
                app.rightInMillis = app.dateArray.get(0);
                Log.e("", "size" + app.dateArray.size());
                Log.e("", "right" + DateUtil.getDateString(app.rightInMillis, DateUtil.RANGE_DAY));
                Log.e("", "left" + DateUtil.getDateString(app.leftInMillis, DateUtil.RANGE_DAY));

            }
        }
        app.photoTotalNum = photoArray.size();
        new ClusteringForMapTask(photoArray, latArray, lngArray).execute();

    }

    public void initializeCursor(Cursor cursor) {
        if (cursor != null)
            cursor = null;
    }

    public ArrayList<Integer> getCurrentBucket() {
        return photoArray;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        rootView = inflater.inflate(R.layout.grid_fragment, container, false);
        gAdapter = new GridCursorAdapter(getActivity(), cursor, flag);
        mScaleGestureDetector = new ScaleGestureDetector(getActivity(), new MyScaleGestureListener());
        /** Getting a reference to gridview of the MainActivity layout */
        gridView = (ExpandableGridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(gAdapter);

      /*  gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mScaleGestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });*/

        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int  pId = 0 ; //to prevent touch in imageView after scaling
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        pId = motionEvent.getPointerId(motionEvent.getActionIndex());
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.e(TAG, "gridView ACTION_POINTER_DOWN ");
                     //   pId = motionEvent.getPointerId(motionEvent.getActionIndex());
                         pId = 1;
                        //    Log.e(TAG, " pId1: " + pId );
                        gridView.setVerticalScrollBarEnabled(false);
                        gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                        gridView.setNumColumns(GRID_COLUMN[gridColumnCount]);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(pId <= 0 && imageNo > 0) {
                            //        Log.e(TAG, " pId2: " + pId );
                            Log.e(TAG, " gridView " + "MotionEvent.ACTION_UP ");
                            Intent intent = new Intent(getActivity(), PhotoDetailActivity2.class);
                            intent.putExtra("com.yhsoft.photrace.position", imageNo);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().startActivity(intent);
                        }
                        return true;

                 /*   case MotionEvent.ACTION_MOVE:

                        Log.e(TAG, "gridView ACTION_MOVE");
                        mScaleGestureDetector.onTouchEvent(motionEvent);
                        return true;*/

                    case MotionEvent.ACTION_POINTER_UP:
                        Log.e(TAG, "gridView ACTION_MOVE");
                        mScaleGestureDetector.onTouchEvent(motionEvent);
                        return true;
                    case MotionEvent.ACTION_OUTSIDE:
                      //  pId = 1;
                        return true;
                }

                mScaleGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
        return rootView;

    }

    public void reloadBucket() {
        init();
        Log.e(TAG, "count: " + gAdapter.getCount());
        //app.photoTotalNum = gAdapter.getCount();
        gAdapter.changeCursor(cursor);
        gAdapter.notifyDataSetChanged();
        gridView.invalidate();
    }

    public void reloadBucketForMap() {
        init2();
        Log.e(TAG, "count: " + gAdapter.getCount());
        //app.photoTotalNum = gAdapter.getCount();
        gAdapter.changeCursor(mapCursor);
        gAdapter.notifyDataSetChanged();
        gridView.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gridView = null;

    }

    private static final class InsetDecoration extends RecyclerView.ItemDecoration {
        private int mInsets;

        public InsetDecoration(Context context) {
            mInsets = context.getResources().getDimensionPixelSize(R.dimen.card_insets);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //We can supply forced insets for each item view here in the Rect
            outRect.set(mInsets, mInsets, mInsets, mInsets);
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView mDate;
        TextView mCount;
        ExpandableGridView mGrid;

        public ListViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.mtt_photo_date);
            mCount = (TextView) itemView.findViewById(R.id.mtt_photo_count);
            mGrid = (ExpandableGridView) itemView.findViewById(R.id.mtt_photo_grid);
        }

        public void bindPhoto(ArrayList<Integer> photos) {
            mGrid.setAdapter(gAdapter);
        }
    }

    public class RecyclerListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        Context mContext;
        ListViewHolder mListViewHolder;

        public RecyclerListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.fragment_mytimetrace_bucket_item, viewGroup, false);
            mListViewHolder = new ListViewHolder(inflate);
            DebugLog.e("onCreateViewHolder");
            return mListViewHolder;
        }


        @Override
        public void onBindViewHolder(ListViewHolder listViewHolder, int position) {
            //listViewHolder.mDate.setText(mItemArray.get(position).getDate());
            //listViewHolder.mCount.setText(" (" + mItemArray.get(position).getCount() + ") ");
            //listViewHolder.bindPhoto(mItemArray.get(position).getPhotoItem());

            listViewHolder.mGrid.setVerticalScrollBarEnabled(false);
            listViewHolder.mGrid.setOverScrollMode(View.OVER_SCROLL_NEVER);
            listViewHolder.mGrid.setNumColumns(GRID_COLUMN[gridColumnCount]);

            DebugLog.e("Position / Column number : " + position + " / " + GRID_COLUMN[gridColumnCount]);
        }

        @Override
        public int getItemCount() {
            return 0;
        }

    }


    public class GridCursorAdapter extends CursorAdapter {
        SquareImageView imageView;
        ImageView imageTagView ;
        public GridCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);

        }

        @Override
        public int getCount() {
            return super.getCount();
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_square_photo_item, parent, false);
            imageView = (SquareImageView) view.findViewById(R.id.photo_imageview);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            gridView.setVerticalScrollBarEnabled(false);
            gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            gridView.setNumColumns(GRID_COLUMN[gridColumnCount]);
            final int imageNum = cursor.getInt(0);
            final double latExist = cursor.getDouble(cursor.getColumnIndex("latitude")); //예외로 떨어짐
            final double lngExist = cursor.getDouble(cursor.getColumnIndex("longitude"));
            imageView = (SquareImageView) view.findViewById(R.id.photo_imageview);
            imageView.setTag(imageNum);
            if(latExist == 0.0 && lngExist == 0.0) {
                imageTagView = (ImageView) view.findViewById(R.id.photo_image_tag);
                imageTagView.setVisibility(View.VISIBLE);
                imageTagView.setImageResource(R.drawable.icon_nolocation_xh);
            }
            MediaUtil.setThumbnail(imageView, imageNum);
           /* if (imageView != null) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), PhotoDetailActivity2.class);
                        intent.putExtra("com.yhsoft.photrace.position", imageNum);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
            }*/
            if (imageView != null) {
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {
                        switch (motionEvent.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN:
                                Log.e(TAG, "imageView MotionEvent.ACTION_DOWN ");
                                imageNo = (int) v.getTag();
                                return false;
                  /*      case MotionEvent.ACTION_SCROLL:
                            Log.e(TAG, "MotionEvent.SCROLL");
                          //  return true;

                        case MotionEvent.ACTION_MOVE:
                            Log.e(TAG, "MotionEvent.ACTION_MOVE ");
                            mScaleGestureDetector.onTouchEvent(motionEvent);
                            return true;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            Log.e(TAG, "ACTION_POINTER_DOWN ");
                                 gridView.setVerticalScrollBarEnabled(false);
                                 gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                                 gridView.setNumColumns(GRID_COLUMN[gridColumnCount]);
                            return false;
                        case MotionEvent.ACTION_UP:
                            Log.e(TAG, "MotionEvent.ACTION_UP ");
                            Intent intent = new Intent(getActivity(), PhotoDetailActivity2.class);
                            intent.putExtra("com.yhsoft.photrace.position", imageNum);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().startActivity(intent);
                            return false;
                        case MotionEvent.ACTION_POINTER_UP:
                            Log.e(TAG, "ACTION_POINTER_UP");
                                 mScaleGestureDetector.onTouchEvent(motionEvent);
                            return false; //차기 우선순위 이벤트 핸들러로 이벤트를 전달 */
                        /*case MotionEvent.ACTION_UP:
                            Log.e(TAG, "MotionEvent.ACTION_UP ");
                            Intent intent = new Intent(getActivity(), PhotoDetailActivity2.class);
                            intent.putExtra("com.yhsoft.photrace.position", imageNum);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().startActivity(intent);
                            return false;*/
                        }
                        return false;
                    }
                });
            }
        }

    }

    public void refreshList() {
        mRecyclerListAdapter.notifyDataSetChanged();
        mBucketList.invalidate();
    }

    public ArrayList<Integer> getAllBucket(ArrayList<PhotoBucketItem> list) {
        ArrayList<Integer> allBucket = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPhotoItem().size() > 0) {
                allBucket.addAll(list.get(i).getPhotoItem());
            }
        }
        return allBucket;
    }



    public class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            Log.d("PhotoViewFragment", "scalefactor: " + scaleFactor);
            if (scaleFactor < 1) {
                gridColumnCount++;
                if (gridColumnCount == GRID_COLUMN.length) {
                    gridColumnCount = GRID_COLUMN.length - 1;
                }
            } else {
                gridColumnCount--;
                if (gridColumnCount < 0) {
                    gridColumnCount = 0;
                }
            }
            gAdapter.notifyDataSetChanged();
        }
    }

}