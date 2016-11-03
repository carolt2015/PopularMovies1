package com.example.caroline.popularmovies.app;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

//https://developer.android.com/reference/android/os/Parcelable.html
public class Movies implements Parcelable {
    private String title;
    private String poster_path;
    private String get_poster;
    private String overview;
    private String rating;
    private String release_date;
    private final String LOG_TAG = Movies.class.getSimpleName();


    public Movies() {
    }

    public Movies(JSONObject movieItem) throws JSONException {
        try {
            this.title = movieItem.getString("original_title");
            get_poster = movieItem.getString("poster_path");
            this.poster_path = buildPosterPath(get_poster);
            this.overview = movieItem.getString("overview");
            this.rating = movieItem.getString("vote_average");
            this.release_date = movieItem.getString("release_date");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error", e);
        }
    }

    private String buildPosterPath(String poster) throws MalformedURLException {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String posterSize = "w185";
        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendPath(posterSize)
                .appendPath(poster.substring(1))
                .build();

        return uri.toString();
    }

    public String getTitle() {return title;}

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getRating() {
        return rating;
    }

    public String getRelease_date() {
        return release_date;
    }



    private Movies(Parcel in) {
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        rating = in.readString();
        release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(release_date);
    }

    public static final Parcelable.Creator<Movies> CREATOR
            = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}
