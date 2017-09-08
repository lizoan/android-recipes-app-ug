package com.pongodev.recipesapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.utils.Utils;


public class FragmentSummary extends Fragment {

    // Create objects of views.
    private TextView txtTime, txtSummary;

    // Create variables to store data.
    private String time, minutes, serveFor, persons;
    private String cookTime, servings, summary;

    public static FragmentSummary newInstance(String cookTime, String servings, String summary) {
        FragmentSummary fragment = new FragmentSummary();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.ARG_COOK_TIME, cookTime);
        bundle.putString(Utils.ARG_SERVINGS, servings);
        bundle.putString(Utils.ARG_SUMMARY, summary);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Store data that pass from activity to variables.
        cookTime = getArguments().getString(Utils.ARG_COOK_TIME);
        servings = getArguments().getString(Utils.ARG_SERVINGS);
        summary = getArguments().getString(Utils.ARG_SUMMARY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary,container,false);

        // Connect view objects and view id on xml.
        txtTime = (TextView) rootView.findViewById(R.id.txtTime);
        txtSummary = (TextView) rootView.findViewById(R.id.txtSummary);

        // Get data from string resources.
        time = getResources().getString(R.string.cook_time);
        minutes = getResources().getString(R.string.minutes);
        serveFor = getResources().getString(R.string.serve_for);
        persons = getResources().getString(R.string.persons);

        // Set data to textviews.
        txtTime.setText(time+" "+cookTime+" "+minutes+", "+
                serveFor+" "+servings+" "+persons);
        txtSummary.setText(summary);

        return rootView;
    }

}
