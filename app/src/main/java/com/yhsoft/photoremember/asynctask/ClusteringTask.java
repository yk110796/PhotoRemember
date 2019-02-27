package com.yhsoft.photoremember.asynctask;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.yhsoft.photoremember.database.DataBaseHelper;
import com.yhsoft.photoremember.database.model.Photo;

import java.util.ArrayList;

/**
 * Created by User on 2015-06-11.
 */
public class ClusteringTask extends AsyncTask<Void, Void, Boolean> {

    private ArrayList<Integer> mLocationArrayList = new ArrayList<>();
    //photos for clustering which is included in time,region range
    private ArrayList<Photo> offsetItem = new ArrayList<>();

    private LatLngBounds bounds;

    public ClusteringTask(ArrayList<Integer> integerArrayList) {
        super();
        mLocationArrayList = integerArrayList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        LatLngBounds.Builder mLatlongBuilder = new LatLngBounds.Builder();
        for (int i = 0; i < mLocationArrayList.size(); i++) {
            Photo item = DataBaseHelper.getInstance().selectPhoto(mLocationArrayList.get(i));
            offsetItem.add(new Photo(new LatLng(item.getLatitude(), item.getLongitude())));
            mLatlongBuilder.include(new LatLng(item.getLatitude(), item.getLongitude()));
        }

        if (mLocationArrayList.size() > 0 && offsetItem.size() > 0) {
            bounds = mLatlongBuilder.build();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //BusProvider.getBus().post(new onClusteringEvent(bounds, offsetItem));
    }
}

