package com.example.caroline.popularmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    public static class MovieDetailFragment extends Fragment {
        private String movie_path;
        String title;
        String overview;
        String rating;
        String release_date;
        Movies mMovies;

        private static final String MOVIE_PARCELABLE_SAVED = "saved_instance_state";

        public MovieDetailFragment() {
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(MOVIE_PARCELABLE_SAVED, mMovies);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = getActivity().getIntent();
            if (savedInstanceState == null) {
                mMovies = intent.getParcelableExtra(MovieFragment.MOVIE_PARCELABLE_KEY);
            } else {
                mMovies = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED);
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            TextView movie_title = (TextView) rootView.findViewById(R.id.movie_title);
            ImageView img = (ImageView) rootView.findViewById(R.id.movie_detail_image);
            TextView movie_overview = (TextView) rootView.findViewById(R.id.movie_overview);
            TextView movie_rating = (TextView) rootView.findViewById(R.id.movie_rating);
            TextView movie_release = (TextView) rootView.findViewById(R.id.movie_release);


            title = mMovies.getTitle();
            movie_title.setText(title);

            movie_path = mMovies.getPoster_path();
            Picasso.with(getActivity())
                    .load(movie_path)
                    .into(img);

            overview = mMovies.getOverview();
            movie_overview.setText(overview);

            rating = mMovies.getRating();
            movie_rating.setText("Rating:  " + rating + " /10");

            release_date = mMovies.getRelease_date();
            movie_release.setText("Release date: " + release_date);

            return rootView;

        }

    }
}