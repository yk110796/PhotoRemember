package com.yhsoft.photoremember.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.activity.PhotoDetailActivity2;
import com.yhsoft.photoremember.asynctask.MapMovePhotoBucketTask;
import com.yhsoft.photoremember.asynctask.PhotoBucketTask;
import com.yhsoft.photoremember.asynctask.PickModePhotoBucketTask;
import com.yhsoft.photoremember.event.InitialMapPhotoBucketEvent;
import com.yhsoft.photoremember.event.InitialPhotoBucketEvent;
import com.yhsoft.photoremember.event.MapPhotoBucketEndEvent;
import com.yhsoft.photoremember.event.PhotoBucketEndEvent;
import com.yhsoft.photoremember.ui.PhotoBucketItem;
import com.yhsoft.photoremember.util.BusProvider;
import com.yhsoft.photoremember.util.DebugLog;
import com.yhsoft.photoremember.util.GeoUtil;
import com.yhsoft.photoremember.util.MediaUtil;
import com.yhsoft.photoremember.util.Preference;
import com.yhsoft.photoremember.view.ExpandableGridView;
import com.yhsoft.photoremember.view.SquareImageView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.v7.widget.RecyclerView.ItemDecoration;
import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static android.support.v7.widget.RecyclerView.State;
import static android.support.v7.widget.RecyclerView.ViewHolder;
import static android.view.LayoutInflater.from;
import static android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import static android.view.View.OVER_SCROLL_NEVER;
import static android.view.View.OnClickListener;
import static android.view.View.OnTouchListener;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static butterknife.ButterKnife.inject;
import static butterknife.ButterKnife.reset;
import static com.yhsoft.photoremember.PhoTrace.getContext;
import static com.yhsoft.photoremember.R.dimen;
import static com.yhsoft.photoremember.R.dimen.card_insets;
import static com.yhsoft.photoremember.R.id;
import static com.yhsoft.photoremember.R.id.common_recyclerview;
import static com.yhsoft.photoremember.R.id.mtt_photo_count;
import static com.yhsoft.photoremember.R.id.mtt_photo_date;
import static com.yhsoft.photoremember.R.id.mtt_photo_grid;
import static com.yhsoft.photoremember.R.id.photo_image_tag;
import static com.yhsoft.photoremember.R.id.photo_imageview;
import static com.yhsoft.photoremember.R.layout;
import static com.yhsoft.photoremember.R.layout.fragment_mytimetrace_bucket_item;
import static com.yhsoft.photoremember.R.layout.fragment_square_photo_item;
import static com.yhsoft.photoremember.R.layout.layout_common_recycler;
import static com.yhsoft.photoremember.util.BusProvider.getBus;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static com.yhsoft.photoremember.util.GeoUtil.AddressCompleteListener;
import static com.yhsoft.photoremember.util.MediaUtil.setThumbnail;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_DATE_PICK_MODE;
import static com.yhsoft.photoremember.util.Preference.getBoolean;

public class PhotoBucketFragment extends Fragment {
    private View rootView = null;
    private ArrayList<PhotoBucketItem> mItemArray = new ArrayList<>();
    private int eventFlag = 0;

    private long mMaxTime, mMinTime;

    ScaleGestureDetector mScaleGestureDetector;
    RecyclerListAdapter mRecyclerListAdapter;
    LinearLayoutManager mLayoutManager;

    private int[] GRID_COLUMN = {3, 6, 10};
    static int gridColumnCount = 0;

    @InjectView(common_recyclerview)
    RecyclerView mBucketList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PhotoBucketTask().execute();

    }

    public class MyScaleGestureListener extends SimpleOnScaleGestureListener {
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            e("Scale Factor : " + scaleFactor);
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
            mRecyclerListAdapter.notifyDataSetChanged();
            mBucketList.invalidate();
        }
    }

    private static final class InsetDecoration extends ItemDecoration {
        private int mInsets;

        public InsetDecoration(Context context) {
            mInsets = context.getResources().getDimensionPixelSize(card_insets);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            //We can supply forced insets for each item view here in the Rect
            outRect.set(mInsets, mInsets, mInsets, mInsets);
        }
    }

    public class ListViewHolder extends ViewHolder {
        TextView mDate;
        TextView mCount;
        ExpandableGridView mGrid;

        public ListViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(mtt_photo_date);
            mCount = (TextView) itemView.findViewById(mtt_photo_count);
            mGrid = (ExpandableGridView) itemView.findViewById(mtt_photo_grid);
        }

        public void bindPhoto(ArrayList<Integer> photos) {
            mGrid.setAdapter(new ImageGridAdapter(photos));
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
            View inflate = from(mContext).inflate(fragment_mytimetrace_bucket_item, viewGroup, false);
            mListViewHolder = new ListViewHolder(inflate);
            e("onCreateViewHolder");
            return mListViewHolder;
        }


        @Override
        public void onBindViewHolder(ListViewHolder listViewHolder, int position) {
            //if(mItemArray.get(position).getCountNum()!=0) {
            listViewHolder.mDate.setText(mItemArray.get(position).getDate());
            listViewHolder.mCount.setText(" (" + mItemArray.get(position).getCount() + ") ");
            // }
            listViewHolder.bindPhoto(mItemArray.get(position).getPhotoItem());
            Log.e("RecyclerListAdapter", "RecyclerListAdapter: " + mItemArray.get(position).getPhotoItem());

            listViewHolder.mGrid.setVerticalScrollBarEnabled(false);
            listViewHolder.mGrid.setOverScrollMode(OVER_SCROLL_NEVER);
            listViewHolder.mGrid.setNumColumns(GRID_COLUMN[gridColumnCount]);

            e("Position / Column number : " + position + " / " + GRID_COLUMN[gridColumnCount]);
        }

        @Override
        public int getItemCount() {
            return mItemArray.size();
        }

    }

    public class ImageGridAdapter extends ArrayAdapter<Integer> {
        SquareImageView imageView;
        ImageView mTagView;
        ArrayList<Integer> photoArray;
        ArrayList<Integer> allphotoArray;

        public ImageGridAdapter(ArrayList<Integer> photos) {
            super(getActivity(), fragment_square_photo_item, photos);
            photoArray = photos;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(fragment_square_photo_item, parent, false);
                imageView = (SquareImageView) view.findViewById(photo_imageview);
                mTagView = (ImageView) view.findViewById(photo_image_tag);
            }

            setThumbnail(imageView, photoArray.get(position));

            if (imageView != null) {
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        e("set on click listener");
                        allphotoArray = getAllBucket2(mItemArray);
                        Intent intent = new Intent(getActivity(), PhotoDetailActivity2.class);
                        /* PhotoDetailActivity : has no pinch zoom out function
                         * PhotoDetailActivity2 : has pinch zoom in/out function , but bad picture
                         */
                        intent.putExtra("com.yhsoft.photrace.photoArray", photoArray);
                        intent.putExtra("com.yhsoft.photrace.allphotoArray", allphotoArray);
                        intent.putExtra("com.yhsoft.photrace.position", position);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
            }
            return view;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(layout_common_recycler, container, false);
        inject(this, rootView);


        mBucketList.setHasFixedSize(false);
        mBucketList.addItemDecoration(new InsetDecoration(getActivity()));
        mBucketList.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBucketList.setLayoutManager(mLayoutManager);
        mRecyclerListAdapter = new RecyclerListAdapter(getActivity());
        mBucketList.setAdapter(mRecyclerListAdapter);

        mScaleGestureDetector = new ScaleGestureDetector(getActivity(), new MyScaleGestureListener());
        mBucketList.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mScaleGestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getBus().unregister(this);

    }

    public ArrayList<Integer> getCurrentBucket() {
        int position = mLayoutManager.findFirstVisibleItemPosition();
        e("Current position : " + position);
        if (position == -1) {
            makeText(getActivity().getApplicationContext(), "null", LENGTH_SHORT);
            return null;
        }
        return mItemArray.get(position).getPhotoItem();
    }

    public ArrayList<Integer> getAllBucket2(ArrayList<PhotoBucketItem> list) {
        ArrayList<Integer> allBucket = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPhotoItem().size() > 0) {
                allBucket.addAll(list.get(i).getPhotoItem());
            }
        }
        return allBucket;
    }

    //*****
    public void reloadAllBuckets() {

        mItemArray.clear();
        mRecyclerListAdapter.notifyDataSetChanged();
        mBucketList.invalidate();

        if (getBoolean(getContext(), KEY_PHOTO_DATE_PICK_MODE)) {
            new PickModePhotoBucketTask().execute();
        } else {
            new PhotoBucketTask().execute();
        }
    }

    public void reloadAllBucketsMap() {

        mItemArray.clear();
        mRecyclerListAdapter.notifyDataSetChanged();
        mBucketList.invalidate();

        new MapMovePhotoBucketTask().execute();
    }

    public void refreshList() {
        mRecyclerListAdapter.notifyDataSetChanged();
        mBucketList.invalidate();
    }

    @Subscribe
    public void onAllPhotoBucketComplete(PhotoBucketEndEvent event) {
        if (event != null) {
            mItemArray = event.getList();
            //여기잘볼것
            ArrayList<Integer> alist = getAllBucket2(mItemArray);
            getBus().post(new InitialPhotoBucketEvent(alist));
            refreshList();
        }
    }

    @Subscribe
    public void onAllMapPhotoBucketComplete(MapPhotoBucketEndEvent event) {
        if (event != null) {
            mItemArray = event.getList();
            mMaxTime = event.getMaxTime();
            mMinTime = event.getMinTime();
            //여기잘볼 것
            ArrayList<Integer> alist = getAllBucket2(mItemArray);
            getBus().post(new InitialMapPhotoBucketEvent(alist, mMaxTime, mMinTime));
            refreshList();
        }
    }


    public ArrayList<Integer> getFocusedBucket() {
        int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (position != NO_POSITION && mItemArray.size() > 0) {
            return mItemArray.get(position).getPhotoItem();
        } else {
            return null;
        }
    }


    public final class BucketRegionListener implements AddressCompleteListener {
        @Override
        public void onAddressComplete(String region, int index) {
        }
    }


}
