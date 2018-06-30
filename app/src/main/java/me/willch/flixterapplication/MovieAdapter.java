package me.willch.flixterapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.willch.flixterapplication.models.Config;
import me.willch.flixterapplication.models.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    // list of movies
    ArrayList<Movie> movies;

    // config needed for image urls
    Config config;

    // context for rendering image
    Context context;

    // keys used for passing data between activities
    public static final String ITEM_RATING = "vote_average";
    public static final String ITEM_POPULARITY = "popularity";
    public static final String ITEM_TITLE = "original_title";
    public static final String ITEM_OVERVIEW = "overview";

    // initialize with list

    public void setConfig(Config config) {
        this.config = config;
    }

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;

    }

    // creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        // return a new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds an inflated view to an item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the movie at the specified position
        Movie movie = movies.get(position);
        // populate the view with the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        // determine the current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        int round_radius = context.getResources().getInteger(R.integer.radius);
        int round_margin = context.getResources().getInteger(R.integer.margin);
        // TODO - set image using Glide
        final RoundedCornersTransformation roundedCornersTransformation = new RoundedCornersTransformation(round_radius, round_margin);

        final RequestOptions requestOptions = RequestOptions.bitmapTransform(
                roundedCornersTransformation
        );

        String imageUrl = null;

        // if in portrait mode, load poster image
        if (isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // get the correct place holder and image view for the current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .apply(
                        RequestOptions.placeholderOf(placeholderId)
                        .error(placeholderId)
                        .fitCenter()
                )
                .apply(requestOptions)
                .into(imageView);


    }

    // returns the total number of items in teh lsit
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create the viewHolder as a static inner class
    public class  ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;
        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);


        }

//        @Override
//        public void onClick(View view) {
//            // first parameter is the context, second is the class of the activity to launch
//            Intent i = new Intent(context, RatingsAndPopularityActivity.class);
//            // put "extras" into the bundle for access in the edit activity
//            i.putExtra(ITEM_TITLE,  Movie.class.g;
//            i.putExtra(ITEM_OVERVIEW, movie.get(position));
//            i.putExtra(ITEM_RATING, movie.get(position));
//            i.putExtra(ITEM_POPULARITY, position);
//            // brings up the edit activity with the expectation of a result
//            startActivityForResult(i, EDIT_REQUEST_CODE);
//        }
    }
}

