/*
* Copyright (c) 2015 Pongodev. All Rights Reserved.
*/
package com.pongodev.recipesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.listeners.OnTapListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterRecipes extends RecyclerView.Adapter<AdapterRecipes.ViewHolder>
{
    // Create arraylist variables to store data.
    private final ArrayList<String> recipeIds;
    private final ArrayList<String> recipeNames;
    private final ArrayList<String> cookTimes;
    private final ArrayList<String> servings;
    private final ArrayList<String> images;
    private OnTapListener onTapListener;

    private Context mContext;
    private String cookTime, minutes, serveFor, persons;

    public AdapterRecipes(Context context)
    {

        this.recipeIds = new ArrayList<String>();
        this.recipeNames = new ArrayList<String>();
        this.cookTimes = new ArrayList<String>();
        this.servings = new ArrayList<String>();
        this.images = new ArrayList<String>();

        mContext = context;

        cookTime = mContext.getResources().getString(R.string.cook_time);
        minutes = mContext.getResources().getString(R.string.minutes);
        serveFor = mContext.getResources().getString(R.string.serve_for);
        persons = mContext.getResources().getString(R.string.persons);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_recipes, null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position)
    {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onTapListener != null)
                    onTapListener.onTapView(recipeIds.get(position), "");

            }
        });

        // Set data to textview.
        viewHolder.txtRecipeName.setText(recipeNames.get(position));
        viewHolder.txtTime.setText(cookTime+" "+cookTimes.get(position)+" "+minutes+", "+
                serveFor+" "+servings.get(position)+" "+persons);

        int image = mContext.getResources().getIdentifier(images.get(position), "drawable", mContext.getPackageName());
        Picasso.with(mContext)
                .load(image)
                .into(viewHolder.imgRecipe);
    }

    @Override
    public int getItemCount()
    {
        return recipeIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtRecipeName, txtTime;
        RoundedImageView imgRecipe;

        public ViewHolder(View v)
        {
            super(v);
            // Connect views object and views id on xml.
            txtRecipeName = (TextView) v.findViewById(R.id.txtTitle);
            txtTime = (TextView) v.findViewById(R.id.txtSubTitle);
            imgRecipe = (RoundedImageView) v.findViewById(R.id.imgThumbnail);
        }
    }

    public void updateList(
            ArrayList<String> recipeIds,
            ArrayList<String> recipeNames,
            ArrayList<String> cookTimes,
            ArrayList<String> servings,
            ArrayList<String> images)
    {
        this.recipeIds.clear();
        this.recipeIds.addAll(recipeIds);

        this.recipeNames.clear();
        this.recipeNames.addAll(recipeNames);

        this.cookTimes.clear();
        this.cookTimes.addAll(cookTimes);

        this.servings.clear();
        this.servings.addAll(servings);

        this.images.clear();
        this.images.addAll(images);

        this.notifyDataSetChanged();
    }

    public void setOnTapListener(OnTapListener onTapListener)
    {
        this.onTapListener = onTapListener;
    }
}
