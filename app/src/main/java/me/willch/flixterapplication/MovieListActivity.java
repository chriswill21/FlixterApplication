package me.willch.flixterapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import me.willch.flixterapplication.models.Config;
import me.willch.flixterapplication.models.Movie;

public class MovieListActivity extends AppCompatActivity {

    // constants
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    // instance fields
    AsyncHttpClient client;

    // the list of currently playing movies
    ArrayList<Movie> movies;
    // the recycler view
    RecyclerView rvMovies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;

    // image config
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // initialize the client
        client = new AsyncHttpClient();
        // intitialize the lsit of movies
        movies = new ArrayList<>();
        // initialize the adapter -- movies array cannot be reinitialized after this point
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and the adapter
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);


        // get the configuratino on app creation
        getConfiguration();

    }

    // get the list of currently playing movies from the API
    private void getNowPlaying(){
        //create the url
        String url = API_BASE_URL + "/movie/now_playing";

        // set the request paramters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        // execute a GET request expcting a json object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);

                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));



                } catch (JSONException e) {
                    logError("Failure to parse now playing movies", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                logError("Failure to get data from now_playing endpoint", throwable, true);
            }
        });
    }


    // get the configuration from the API
    private void getConfiguration() {
        //create the url
        String url = API_BASE_URL + "/configuration";

        // set the request paramters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        // execute a GET request expcting a json object response
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // get the image base url
                try {
                    config = new Config(response);
                    // get the now playing movie list
                    getNowPlaying();

                    Log.i(TAG,
                            String.format("Loaded configuration with imageBaseUrl %s and posterSize %s",
                                    config.getImageBaseUrl(),
                                    config.getPosterSize()));
                    // pass config to adapter
                    adapter.setConfig(config);

                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                logError("Failed getting configuration", throwable, true);
            }


        });

    }

    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);

        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }


//    private void setupListViewListener() {
//        // set the ListView's itemLongClickListener
//        rvMovies.callOnClick(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                // remove the item in the list at the index given by position
//                items.remove(position);
//                // notify the adapter that the underlying dataset changed
//                itemsAdapter.notifyDataSetChanged();
//                // return true to tell the framework that the long click was consumed
//
//                // store the updated list
//                writeItems();
//
//                Log.i("MainActivity", "Removed item " + position);
//
//                return true;
//            }
//
//        });
//
//        // set the ListView's itemClickListener
//        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // first parameter is the context, second is the class of the activity to launch
//                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
//                // put "extras" into the bundle for access in the edit activity
//                i.putExtra(ITEM_TEXT, items.get(position));
//                i.putExtra(ITEM_POSITION, position);
//                // brings up the edit activity with the expectation of a result
//                startActivityForResult(i, EDIT_REQUEST_CODE);
//            }
//        });
//
//
//    }
}
