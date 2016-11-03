package com.example.caroline.popularmovies.app;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
// Custom Movie adapter
public class MovieAdapter extends ArrayAdapter<Movies> {

    public MovieAdapter(Activity context, List<Movies> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movies movies = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.movie_image_item,
                parent, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_list_item_imageview);

        // Load urls for images using Picasso
        Picasso.with(getContext())
                .load(movies.getPoster_path())
                .into(imageView);

        return rootView;

    }
}
