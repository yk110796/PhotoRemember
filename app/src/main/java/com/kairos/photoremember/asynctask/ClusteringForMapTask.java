package com.kairos.photoremember.asynctask;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.database.model.PhotoMarker;
import com.kairos.photoremember.event.onClusteringEventForMap;
import com.kairos.photoremember.util.BusProvider;

import java.util.ArrayList;

/**
 * Created by User on 2015-06-11.
 */
public class ClusteringForMapTask extends AsyncTask<Void, Void, Boolean> {

    final static double zero = 0.0;

    private ArrayList<Integer> mLocationArrayList = null;
    private ArrayList<Double> latArray = null;
    private ArrayList<Double> lngArray = null;
    private ArrayList<Photo> offsetItem = null;
    private ArrayList<PhotoMarker> offsetMarkerItem = null ;


    public ClusteringForMapTask(ArrayList<Integer> integerArrayList, ArrayList<Double> latArray, ArrayList<Double> lngArray ) {
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
        offsetItem =  new ArrayList<>();
        offsetMarkerItem = new ArrayList<>();

        for(int i = 0; i < mLocationArrayList.size() -1 ; i++){
            if(latArray.get(i) == zero || lngArray.get(i) == zero ){  }
            else {
                offsetItem.add(new Photo(new LatLng(latArray.get(i), lngArray.get(i))));
                offsetMarkerItem.add(new PhotoMarker(
                        new LatLng(latArray.get(i), lngArray.get(i)), mLocationArrayList.get(i)));
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        BusProvider.getBus().post(new onClusteringEventForMap(offsetItem, offsetMarkerItem));
        offsetItem.clear();
        mLocationArrayList.clear();
        latArray.clear();
        lngArray.clear();
        offsetMarkerItem.clear();
    }
}

