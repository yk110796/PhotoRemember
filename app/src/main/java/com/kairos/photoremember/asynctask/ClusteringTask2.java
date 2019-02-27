package com.kairos.photoremember.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.database.model.PhotoMarker;
import com.kairos.photoremember.event.onClusteringEvent;
import com.kairos.photoremember.util.BusProvider;
import java.util.ArrayList;

/**
 * Created by User on 2015-06-11.
 */
public class ClusteringTask2 extends AsyncTask<Void, Void, Boolean> {

    final static double zero = 0.0;

    private ArrayList<Integer> mLocationArrayList = null;
    private ArrayList<Double> latArray = null;
    private ArrayList<Double> lngArray = null;
    //photos for clustering which is included in time, region range
    private ArrayList<Photo> offsetItem = null;
    private ArrayList<PhotoMarker> offsetMarkerItem = null;

    private LatLngBounds bounds;
    public ClusteringTask2(ArrayList<Integer> integerArrayList, ArrayList<Double> latArray, ArrayList<Double> lngArray ) {
        super();
        mLocationArrayList = integerArrayList;
        this.latArray = latArray;
        this.lngArray = lngArray;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        offsetItem = new ArrayList<>();
        offsetMarkerItem  = new ArrayList<>();
        LatLngBounds.Builder mLatlongBuilder = new LatLngBounds.Builder();
        for(int i = 0; i < mLocationArrayList.size() -1 ; i++){
            if(latArray.get(i) == zero || lngArray.get(i) == zero ){}
            else {
                offsetItem.add(new Photo(new LatLng(latArray.get(i), lngArray.get(i))));
       //         Log.e("", "LAT: " + latArray.get(i) + "LNG: " + lngArray.get(i));
                offsetMarkerItem.add(new PhotoMarker(
                        new LatLng(latArray.get(i), lngArray.get(i)), mLocationArrayList.get(i)));
                mLatlongBuilder.include(new LatLng(latArray.get(i), lngArray.get(i)));
            }
        }

        if ( offsetItem.size() > 0) {
            bounds = mLatlongBuilder.build();
            Log.e("", "off count: " + offsetItem.size());
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(bounds != null)
              BusProvider.getBus().post(new onClusteringEvent(bounds, offsetItem, offsetMarkerItem)); // exception often occurs this line
       // BusProvider.getBus().post(new onClusteringEventToPhotoViewFragment());
        offsetItem.clear();
        mLocationArrayList.clear();
        latArray.clear();
        lngArray.clear();
        offsetMarkerItem.clear();
        bounds = null;
    }
}

