package me.willch.flixterapplication.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel // annotation indicates class is Parcelable
public class Movie {

    // values from API
    private String title;
    private  String overview;
    private String posterPath; // only the path
    private String backdropPath;
    private Double rating;
    private Double popularity;

    // initialize from json data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        rating = Double.parseDouble(object.getString("vote_average"));
        popularity = Double.parseDouble(object.getString("popularity"));
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getRating() {
        return rating;
    }

    public Double getPopularity() {
        return popularity;
    }
}





