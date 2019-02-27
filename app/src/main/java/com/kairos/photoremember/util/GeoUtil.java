/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.kairos.photoremember.ui.PlaceItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GeoUtil {

    private static GeoUtil mInstance = null;
    private String currentAddress;
    private static LatLng mLatLng;
    private static ArrayList<LatLng> mLatLngList = new ArrayList<>();
    private ArrayList<PlaceItem> mPlaceResult = new ArrayList<>();
    private PlaceCompleteListener mPlaceListener = null;
    private AddressCompleteListener mAddrListener = null;

    private static final String SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private static final String DETAIL_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String GEOCODE_URL = "http://maps.googleapis.com/maps/api/geocode/json?";
    private static String GOOGLE_API_KEY = "AIzaSyBk5F8zxH6WTJb0hYOP8ps-ffET49Vm6hs";

    private static final String SEARCH_TYPE =
            "airport|amusement_park|aquarium|art_gallery" +
                    "|bakery|bar|bank|beauty_salon|cafe|church" +
                    "|clothing_store|convenience_store|department_store" +
                    "|electronics_store|food|gas_station|hardware_store" +
                    "|home_goods_store|hospital|library|movie_theater" +
                    "|museum|park|post_office|police|restaurant" +
                    "|shopping_mall|stadium|subway_station|train_station|university|zoo";

    public static GeoUtil getInstance(LatLng latlng) {
        if (mInstance == null) {
            mInstance = new GeoUtil();
        }
        mLatLng = latlng;
        return mInstance;
    }

    public static GeoUtil getInstance(ArrayList<LatLng> latlngList) {
        if (mInstance == null) {
            mInstance = new GeoUtil();
        }
        mLatLngList = latlngList;
        return mInstance;
    }

    public interface PlaceCompleteListener {
        void onCompletePlace(ArrayList<PlaceItem> result);
    }

    public interface AddressCompleteListener {
        void onAddressComplete(String region, int index);
    }

    public void startDownload(PlaceCompleteListener listen) {
        mPlaceListener = listen;
        PlaceDownloadTask placeTask = new PlaceDownloadTask();
        placeTask.execute();
    }

    public void startReverseGeocode(AddressCompleteListener listen, int index) {
        mAddrListener = listen;
        ReverseGeoCodeTask addrTask = new ReverseGeoCodeTask(index);
        addrTask.execute();
    }

    public String getJSON(URL url, int timeout) {
        try {
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public class PlaceDownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {
            PlaceItem mItem;
            String queryString = String.format(SEARCH_URL+"location=%f,%f&types=%s&radius=%d&sensor=true&key=%s",
                    mLatLng.latitude, mLatLng.longitude, SEARCH_TYPE, 3000, GOOGLE_API_KEY);
            try {
                URL url = new URL(queryString);
                String result = getJSON(url, 500);
                JSONObject placeObject = new JSONObject(result);
                JSONArray placeArray = placeObject.optJSONArray("results");
                String ret = placeObject.optString("status");
                JSONArray photos;
                String photoRef = null;

                DebugLog.e("Result : " + ret + " Place Array length : " + placeArray.length());

                mPlaceResult.clear();

                for (int i = 0; i < placeArray.length(); i++) {
                    mItem = new PlaceItem();
                    mItem.name = placeArray.optJSONObject(i).optString("name");
                    mItem.vicinity = placeArray.optJSONObject(i).optString("vicinity");
                    mItem.lat = placeArray.optJSONObject(i).optJSONObject("geometry").optJSONObject("location").optDouble("lat");
                    mItem.lng = placeArray.optJSONObject(i).optJSONObject("geometry").optJSONObject("location").optDouble("lng");

                    DebugLog.e("name: " + mItem.name);
                    DebugLog.e("vicinity: " + mItem.vicinity);
                    DebugLog.e("Lat/Lng: " + mItem.lat + ", " + mItem.lng);

                    mItem.photoURL = null;
                    photos = placeArray.optJSONObject(i).optJSONArray("photos");
                    if (photos != null) {
                        photoRef = photos.optJSONObject(0).optString("photo_reference");
                        if (photoRef != null) {
                            String photo_url = String.format(PHOTO_URL+"maxwidth=270&photoreference=%s&sensor=true&key=%s",
                                    photoRef, GOOGLE_API_KEY);
                            mItem.photoURL = new URL(photo_url);
                        }
                    }
                    DebugLog.e("Photo URL : " + mItem.photoURL);
                    String detailRef = placeArray.optJSONObject(i).optString("reference");
                    String detail_url = String.format(DETAIL_URL+"reference=%s&sensor=true&key=%s",
                            detailRef, GOOGLE_API_KEY);
                    mItem.detailURL = new URL(detail_url);
                    DebugLog.e("Detail URL : " + mItem.detailURL);

                    String detail_results = getJSON(new URL(detail_url), 500);
                    JSONObject detailObject;
                    try {
                        if (detail_results != null) {
                            detailObject = new JSONObject(detail_results);
                            mItem.phoneNumber = detailObject.optJSONObject("result").optString("formatted_phone_number");
                            DebugLog.e("Phone number : " + mItem.phoneNumber);
                        } else {
                            DebugLog.e("detail_result is null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mPlaceResult.add(mItem);
                }
                mPlaceListener.onCompletePlace(mPlaceResult);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class ReverseGeoCodeTask extends AsyncTask<Void, Void, String> {
        private String regionText = null;
        private int mReturnIndex;

        public ReverseGeoCodeTask(int index) {
            mReturnIndex = index;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (mLatLngList != null && mLatLngList.size() > 0) {
                for (int index = 0; index < mLatLngList.size(); index++) {
                    String queryString = String.format(GEOCODE_URL + "latlng=%f,%f&sensor=true",
                            mLatLngList.get(index).latitude, mLatLngList.get(index).longitude);

                    try {
                        URL url = new URL(queryString);
                        String result = getJSON(url, 500);
                        JSONObject addressObject = new JSONObject(result);
                        JSONArray addressArray = addressObject.optJSONArray("results").optJSONObject(0).optJSONArray("address_components");
                        String ret = addressObject.optString("status");

                        DebugLog.e("Result : " + ret + " Address Array length : " + addressArray.length());

                        for (int temp = 0; temp < addressArray.length(); temp++) {
                            String region = addressArray.optJSONObject(temp).optJSONArray("types").optString(0);
                            DebugLog.e("Resion : " + region);
                            if (region.equals("locality")) {
                                regionText = addressArray.optJSONObject(temp).optString("long_name");
                                break;
                            }
                        }

                        if (regionText != null) {
                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                return new StringBuffer(regionText).append("...").toString();
            } else {
                return "INVALID";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (mAddrListener != null) {
                mAddrListener.onAddressComplete(regionText, mReturnIndex);
            }
        }
    }
}
