/*
* Copyright (c) 2015 Pongodev. All Rights Reserved.
*/
package com.pongodev.recipesapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.fragments.FragmentAbout;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class ActivityAbout extends ActionBarActivity implements FragmentAbout.OnItemSelectedListener {

    // Create array variables to store about content.
    public static String[] titles, summaries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Get about content from string resources.
        titles = getResources().getStringArray(R.array.about_titles);
        summaries = getResources().getStringArray(R.array.about_summaries);


        // Show the Up button in the action bar.
        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create an instance of FragmentAbout and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            FragmentAbout fragment = new FragmentAbout();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        // Handle menu item in toolbar.
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case android.R.id.home:
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }


    // Add transition when back button pressed.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }


    @Override
    public void onItemSelected(int position) {
        if(position == 4) {
            // When Tell a Friend item in list clicked, open share dialog.
            Intent iShare = new Intent(Intent.ACTION_SEND);
            iShare.setType("text/plain");
            iShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
            iShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.message) + " " + getString(R.string.google_play_url));
            startActivity(Intent.createChooser(iShare, getString(R.string.share)));
        }else if((position == 5) || (position == 6)){
            // When Rate and Review item in list clicked, go to Google Play.
            Intent iRate = new Intent(Intent.ACTION_VIEW);
            iRate.setData(Uri.parse(getString(R.string.google_play_url)));
            startActivity(iRate);
        }
    }
}
