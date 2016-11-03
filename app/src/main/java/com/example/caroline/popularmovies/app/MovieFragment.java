package com.example.caroline.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieFragment extends Fragment {

    MovieAdapter adapter;
    ArrayList<Movies> movieList;

    final String MOVIES_PARCELABLE_LIST = "movies_list";
    public static final String MOVIE_PARCELABLE_KEY = "movie_parcelable";


    public MovieFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_PARCELABLE_LIST)) {
            movieList = new ArrayList<>();
        } else {
            movieList = savedInstanceState.getParcelableArrayList(MOVIES_PARCELABLE_LIST);
        }
        checkConnection();
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_PARCELABLE_LIST, movieList);
        super.onSaveInstanceState(outState);
    }

    public void checkConnection() {
        if (isOnline()) {
            UploadMovies();
        }
    }

    // This boolean method checks for connection.(If no connection, then no need to proceed)
    // pros: does not let the app crash.
    //http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    // Uploads movies according to user preferences from 'Settings'
    public void UploadMovies() {
        UploadMovieTask uploadMovieTask = new UploadMovieTask();
        uploadMovieTask.execute(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.sort_movies_key)
                        , getString(R.string.sort_movie_popular)));
    }

    @Override
    public void onStart() {
        super.onStart();
        UploadMovies();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new MovieAdapter(getActivity(), movieList);
        //Get a reference to the GridView, and attach this adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movie = adapter.getItem(position);
                Intent movieDetail = new Intent(getActivity(), MovieDetail.class)
                        .putExtra(MOVIE_PARCELABLE_KEY, movie);
                startActivity(movieDetail);
                adapter.notifyDataSetChanged();
            }

        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //  ASYNCTASK: https://developer.android.com/reference/android/os/AsyncTask.html
    public class UploadMovieTask extends AsyncTask<String, Void, ArrayList<Movies>> {

        private final String LOG_TAG = UploadMovieTask.class.getSimpleName();

        public ArrayList<Movies> getMovieDetailFromJson(String MovieJsonStr)
                throws JSONException {

            ArrayList<Movies> moviesList = new ArrayList<>();
            final String RESULTS = "results";

            JSONObject movieJson = new JSONObject(MovieJsonStr);
            JSONArray resultsArrayList = movieJson.getJSONArray(RESULTS);

            for (int i = 0; i < resultsArrayList.length(); i++) {
                JSONObject movieItem = resultsArrayList.getJSONObject(i);
                Movies movies = new Movies(movieItem);
                moviesList.add(movies);
            }
            return moviesList;
        }

        @Override
        protected ArrayList<Movies> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;


            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //Raw data
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDetailFromJson(movieJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<Movies> movies) {

            if (movies != null) {
                adapter.clear();
                adapter.addAll(movies);
                movieList = movies;
            }
            else {
                Toast.makeText(getActivity(),"Please check your internet connection !",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

}














