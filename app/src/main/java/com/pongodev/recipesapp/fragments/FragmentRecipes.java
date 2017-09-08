package com.pongodev.recipesapp.fragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.ads.AdView;
import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.adapters.AdapterRecipes;
import com.pongodev.recipesapp.listeners.OnTapListener;
import com.pongodev.recipesapp.utils.DBHelperFavorites;
import com.pongodev.recipesapp.utils.DBHelperRecipes;
import com.pongodev.recipesapp.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentRecipes extends Fragment {

    // Create objects of views.
    private RecyclerView recyclerView;
    private ProgressBarCircularIndeterminate prgLoading;
    private TextView txtEmpty;
    private AdView adView;

    // Create instance of database helper.
    private DBHelperRecipes dbhelperRecipes;
    private DBHelperFavorites dbhelperFavorites;
    private AdapterRecipes adapterRecipes;

    private OnRecipeSelectedListener mCallback;

    private ArrayList<ArrayList<Object>> data;

    private String currentKey = Utils.ARG_DEFAULT_CATEGORY_ID;
    private String activePage = Utils.ARG_CATEGORY;

    // Create arraylist variables to store data.
    private ArrayList<String> recipeIds = new ArrayList<String>();
    private ArrayList<String> recipeNames = new ArrayList<String>();
    private ArrayList<String> cookTimes = new ArrayList<String>();
    private ArrayList<String> servings = new ArrayList<String>();
    private ArrayList<String> images = new ArrayList<String>();

    public interface OnRecipeSelectedListener {
        public void onRecipeSelected(String ID, String CategoryName);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save the current article selection in case we need to recreate the fragment
        outState.putString(Utils.ARG_KEY, currentKey);
        outState.putString(Utils.ARG_PAGE, Utils.ARG_CATEGORY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentKey = savedInstanceState.getString(Utils.ARG_KEY);
            activePage = savedInstanceState.getString(Utils.ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);


        if (savedInstanceState != null) {
            currentKey = savedInstanceState.getString(Utils.ARG_KEY);
            activePage = savedInstanceState.getString(Utils.ARG_PAGE);
        }

        // Connect view objects and view id on xml.
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        prgLoading = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.prgLoading);
        adView = (AdView) rootView.findViewById(R.id.adView);
        txtEmpty = (TextView) rootView.findViewById(R.id.txtEmpty);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        boolean isAdmobVisible = Utils.admobVisibility(adView, Utils.ARG_ADMOB_VISIBILITY);
        if(isAdmobVisible)
            Utils.loadAdmob(adView);

        // Create object of database helpers.
        dbhelperRecipes = new DBHelperRecipes(getActivity());
        dbhelperFavorites = new DBHelperFavorites(getActivity());

        // Create database of recipes.
        try {
            dbhelperRecipes.createDataBase();
            dbhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open recipes database.
        dbhelperRecipes.openDataBase();
        dbhelperFavorites.openDataBase();

        adapterRecipes = new AdapterRecipes(getActivity());


        adapterRecipes.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String ID, String CategoryName) {
                mCallback.onRecipeSelected(ID, "");
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.

        Bundle args = getArguments();
        if (args != null) {
            // Set recipes based on argument passed in
            updateRecipes(args.getString(Utils.ARG_KEY), args.getString(Utils.ARG_PAGE));
        } else if (!currentKey.equals("")) {
            // Set recipes based on saved instance state defined during onCreateView
            updateRecipes(currentKey, activePage);
        }
    }

    public void updateRecipes(String key, String page){

        dbhelperRecipes.openDataBase();
        dbhelperFavorites.openDataBase();
        currentKey = key;
        activePage = page;
        new syncGetData().execute();
    }

    public class syncGetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            recipeIds.clear();
            recipeNames.clear();
            cookTimes.clear();
            servings.clear();
            images.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getDataFromDatabase(currentKey);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            prgLoading.setVisibility(View.GONE);

            if(recipeIds.isEmpty()) {
                txtEmpty.setVisibility(View.VISIBLE);
            }else {
                txtEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapterRecipes.updateList(recipeIds, recipeNames, cookTimes, servings, images);
            }
            recyclerView.setAdapter(adapterRecipes);


        }
    }

    public void getDataFromDatabase(String key){

        if(activePage.equals(Utils.ARG_CATEGORY)) {
            data = dbhelperRecipes.getAllRecipesData(key);
        }else if(activePage.equals(Utils.ARG_SEARCH)){
            data = dbhelperRecipes.getRecipesByName(key);
        }else if(activePage.equals(Utils.ARG_FAVORITES)){
            data = dbhelperFavorites.getAllRecipesData();
        }


        for(int i = 0;i < data.size();i++){
            ArrayList<Object> row = data.get(i);

            recipeIds.add(row.get(0).toString());
            recipeNames.add(row.get(1).toString());
            cookTimes.add(row.get(2).toString());
            servings.add(row.get(3).toString());
            images.add(row.get(4).toString());
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnRecipeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRecipeSelectedListener");
        }
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
        if (adView != null) {
            adView.resume();
        }
        super.onResume();
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        dbhelperRecipes.close();
        dbhelperFavorites.close();
        super.onDestroy();
    }
}
