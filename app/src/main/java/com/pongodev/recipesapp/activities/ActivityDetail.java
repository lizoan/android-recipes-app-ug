/*
* Copyright (c) 2015 Pongodev. All Rights Reserved.
*/
package com.pongodev.recipesapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.adapters.AdapterDetailPager;
import com.pongodev.recipesapp.fragments.FragmentInfo;
import com.pongodev.recipesapp.fragments.FragmentSummary;
import com.pongodev.recipesapp.utils.DBHelperFavorites;
import com.pongodev.recipesapp.utils.DBHelperRecipes;
import com.pongodev.recipesapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ActivityDetail extends AppCompatActivity implements View.OnClickListener {

    // Create view objects.
    private KenBurnsView imgRecipe;
    private TextView txtRecipeName, txtCategory;
    private LinearLayout lytDetail, lytTitle;
    private ProgressBarCircularIndeterminate prgLoading;
    private ButtonFloat btnFavorite;
    private AdView adView;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private InterstitialAd interstitialAd;

    // Create variables to store data.
    private String selectedId;
    private String recipeId, categoryId, categoryName, recipeName, cookTime, servings, summary, ingredients, steps, recipeImage;

    // Create instance of database helper.
    private DBHelperRecipes dbhelperRecipes;
    private DBHelperFavorites dbhelperFavorites;

    // Create instance of adapter
    private AdapterDetailPager adapterDetailPager;

    // Create fragments for pager.
    private List<Fragment> pagerFragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Stored selected ID that passed from previous activity.
        Intent i = getIntent();
        selectedId = i.getStringExtra(Utils.ARG_KEY);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Connect view objects with ids in xml layout.
        prgLoading = (ProgressBarCircularIndeterminate) findViewById(R.id.prgLoading);
        lytDetail = (LinearLayout) findViewById(R.id.lytDetail);
        lytTitle = (LinearLayout) findViewById(R.id.lytTitle);
        imgRecipe = (KenBurnsView) findViewById(R.id.imgRecipe);
        txtRecipeName = (TextView) findViewById(R.id.txtRecipeName);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        btnFavorite = (ButtonFloat) findViewById(R.id.btnFavorite);
        adView = (AdView) findViewById(R.id.adView);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        btnFavorite.setOnClickListener(this);

        // Check ad visibility. If visible, display ad banner and interstitial.
        interstitialAd = new InterstitialAd(this);
        boolean isAdmobVisible = Utils.admobVisibility(adView, Utils.ARG_ADMOB_VISIBILITY);
        if(isAdmobVisible) {
            Utils.loadAdmob(adView);

            // When interstitialTrigger equals ARG_TRIGGER_VALUE, display interstitial ad.
            int interstitialTrigger = Utils.loadPreferences(Utils.ARG_TRIGGER, this);
            if(interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                Utils.loadAdmobInterstitial(interstitialAd, this);
                Utils.savePreferences(Utils.ARG_TRIGGER, 0, this);
            }else{
                Utils.savePreferences(Utils.ARG_TRIGGER, (interstitialTrigger+1), this);
            }
        }

        // Create object of database helpers.
        dbhelperRecipes = new DBHelperRecipes(this);
        dbhelperFavorites = new DBHelperFavorites(this);

        // Create database of recipes and favorites.
        try {
            dbhelperRecipes.createDataBase();
            dbhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open recipes and favorites database.
        dbhelperRecipes.openDataBase();
        dbhelperFavorites.openDataBase();

        // Set button icon based on recipe availability in favorites database.
        if(dbhelperFavorites.isDataAvailable(selectedId)) {
            btnFavorite.setIconDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline_white_36dp));
        }else{
            btnFavorite.setIconDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_36dp));
        }

        // Get data from recipes database.
        new syncGetData().execute();

        // Handle menu item in toolbar.
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.menuShare:
                        createShareIntent();
                        return true;
                    case android.R.id.home:
                        finish();
                        return true;
                    default:
                        return true;
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnFavorite:
                if(dbhelperFavorites.isDataAvailable(selectedId)) {
                    // If recipe has already available in favorites database. display dialog to confirm remove it.
                    new MaterialDialog.Builder(ActivityDetail.this)
                            .title(R.string.confirm)
                            .content(R.string.confirm_message)
                            .positiveText(R.string.remove)
                            .negativeText(R.string.cancel)
                            .positiveColorRes(R.color.color_primary)
                            .negativeColorRes(R.color.color_primary)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    boolean result = dbhelperFavorites.deleteRecipeFromFavorites(selectedId);
                                    if (result) {
                                        new SnackBar(ActivityDetail.this, getString(R.string.success_remove)).show();
                                        btnFavorite.setIconDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_36dp));
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    // If recipes is not available in favorites database. add to favorites database.
                    boolean result = dbhelperFavorites.addRecipeToFavorites(recipeId, categoryId, recipeName,
                            cookTime, servings, summary,
                            ingredients, steps, recipeImage);

                    if(result){
                        new SnackBar(ActivityDetail.this, getString(R.string.success_add_data)).show();
                        btnFavorite.setIconDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline_white_36dp));
                    }
                }
                break;

        }
    }

    public class syncGetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgLoading.setVisibility(View.VISIBLE);
            lytDetail.setVisibility(View.GONE);
            lytTitle.setVisibility(View.GONE);
            btnFavorite.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getDataFromDatabase(selectedId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int image = getResources().getIdentifier(recipeImage, "drawable", getPackageName());

            Picasso.with(getApplicationContext())
                    .load(image)
                    .into(imgRecipe);

            createPager();

            prgLoading.setVisibility(View.GONE);
            lytDetail.setVisibility(View.VISIBLE);
            lytTitle.setVisibility(View.VISIBLE);
            btnFavorite.setVisibility(View.VISIBLE);

        }
    }

    // Create pager for Summary, Ingredients, and Steps.
    public void createPager(){
        pagerFragments.add(FragmentSummary.newInstance(cookTime, servings, summary));
        pagerFragments.add(FragmentInfo.newInstance(ingredients));
        pagerFragments.add(FragmentInfo.newInstance(steps));

        adapterDetailPager = new AdapterDetailPager(getSupportFragmentManager(), this, pagerFragments);
        pager.setAdapter(adapterDetailPager);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
    }

    // Get data from database.
    public void getDataFromDatabase(String id){
        ArrayList<Object> row = dbhelperRecipes.getRecipeDetail(id);

            recipeId = row.get(0).toString();
            recipeName = row.get(1).toString();
            categoryId = row.get(2).toString();
            categoryName = row.get(3).toString();
            cookTime = row.get(4).toString();
            servings = row.get(5).toString();
            summary = row.get(6).toString();
            ingredients = row.get(7).toString();
            steps = row.get(8).toString();
            recipeImage = row.get(9).toString();

            txtRecipeName.setText(recipeName);
            txtCategory.setText(categoryName);
    }

    // Create share intent when share item click.
    Intent createShareIntent() {
        String intro = getResources().getString(R.string.intro_message);
        String extra = getResources().getString(R.string.extra_message);
        String gPlayURL = getResources().getString(R.string.google_play_url);
        String appName = getResources().getString(R.string.app_name);
        String here = getResources().getString(R.string.here);
        String message = intro+" "+recipeName+extra+" "+appName+" "+here+" "+gPlayURL;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(i, getResources().getString(R.string.share_to)));
        return i;
    }


    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        dbhelperRecipes.close();
        super.onDestroy();
    }

    // Add transition when back button pressed.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    // Configuration in Android API 21 to set window to full screen.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    // Check parent activity to call.
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent= getIntent();
        // Get the parent class name.
        String className = parentIntent.getStringExtra(Utils.ARG_PARENT_ACTIVITY);

        Intent newIntent=null;
        try {
            // Open parent activity.
            newIntent = new Intent(this,Class.forName(getApplicationContext().getPackageName()+"."+className));

            overridePendingTransition(R.anim.open_main, R.anim.close_next);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

}
