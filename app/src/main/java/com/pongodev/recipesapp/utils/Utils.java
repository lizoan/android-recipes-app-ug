package com.pongodev.recipesapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.pongodev.recipesapp.R;

public class Utils {

    // Application parameters. do not change this parameters.
    public static final String ARG_PAGE = "page";
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_SEARCH = "search";
    public static final String ARG_FAVORITES = "favorites";
    public static final String ARG_KEY = "key";
    public static final String ARG_TAG_CONTENT = "CONTENT_HERE";
    public static final String ARG_COOK_TIME = "cook_time";
    public static final String ARG_SERVINGS = "servings";
    public static final String ARG_SUMMARY = "summary";
    public static final String ARG_INFO = "info";
    public static final String ARG_PARENT_ACTIVITY = "parent_activity";
    public static final String ARG_ACTIVITY_HOME = "activities.ActivityHome";
    public static final String ARG_ACTIVITY_SEARCH = "activities.ActivitySearch";
    public static final String ARG_ACTIVITY_FAVORITES = "activities.ActivityFavorites";
    public static final String ARG_TRIGGER = "trigger";
    public static final int ARG_GONE = 0;
    public static final int ARG_DEBUGGING = 1;

    // Configurable parameters. you can configure these parameter.
    // Set database path. It must be similar with package name.
    public static final String ARG_DATABASE_PATH = "/data/data/com.pongodev.recipesapp/databases/";
    // For every recipe detail you want to display interstitial ad
    public static final int ARG_TRIGGER_VALUE = 3;
    // Admob visibility parameter. set 1 to show admob and 0 to hide.
    public static final int ARG_ADMOB_VISIBILITY = 1;
    // Set value to 1 if you are still in development process, and zero if you are ready to publish the app.
    public static final int ARG_ADMOB_DEVELOPMENT_TYPE = 1;
    // Set default category data, you can see the category id in sqlite database.
    public static final String ARG_DEFAULT_CATEGORY_ID = "2";

    public static void loadAdmob(final AdView ad){
        // Create an ad request.
        AdRequest adRequest;
        if(ARG_ADMOB_DEVELOPMENT_TYPE == ARG_DEBUGGING) {
            adRequest = new AdRequest.Builder().
                    addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        }else {
            adRequest = new AdRequest.Builder().build();
        }

        // Start loading the ad.
        ad.loadAd(adRequest);

        ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (ad != null) {
                    ad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static boolean admobVisibility(AdView ad, int parameter){
        ad.setVisibility(parameter);
        if(parameter == ARG_GONE ) {
            ad.setVisibility(View.INVISIBLE);
            return false;
        }else {
            ad.setVisibility(View.VISIBLE);
            return true;
        }
    }

    public static void loadAdmobInterstitial(final InterstitialAd interstitialAd, Context c){

        interstitialAd.setAdUnitId(c.getResources().getString(R.string.interstitial_ad_unit_id));
        // Create an ad request.
        AdRequest adRequest;
        if(ARG_ADMOB_DEVELOPMENT_TYPE == ARG_DEBUGGING) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        }else {
            adRequest = new AdRequest.Builder().build();
        }

        // Start loading the ad.
        interstitialAd.loadAd(adRequest);

        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {

            }

        });

    }

    // Method to load map type setting
    public static int loadPreferences(String param, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        return sharedPreferences.getInt(param, 0);
    }

    // Method to save map type setting to SharedPreferences
    public static void savePreferences(String param, int value, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(param, value);
        editor.apply();
    }
}
