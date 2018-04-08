package com.astoria.movieapp.adapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astoria.movieapp.DetailedActivity;
import com.astoria.movieapp.R;
import com.astoria.movieapp.model.ResultMovie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
    public int getResultMovieList() {
        return resultMovieList == null ? 0 : resultMovieList.size();
    }

    private List<ResultMovie> resultMovieList = new ArrayList<>();
    private final Context context;
    public MovieAdapter(Context context) {
        this.context = context;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final ResultMovie resultMovie = resultMovieList.get(position);
        final String posterPath = resultMovie.getPosterPath();
        holder.imageView.setOnClickListener((view)->
        {
            Intent intent = new Intent(context, DetailedActivity.class);
            intent.putExtra("id", resultMovie.getId());
            intent.putExtra("title", resultMovie.getTitle());
            intent.putExtra("overview", resultMovie.getOverview());
            intent.putExtra("vote", resultMovie.getVoteAverage().toString());
            intent.putExtra("date", resultMovie.getReleaseDate());
            intent.putExtra("poster", posterPath);
            context.startActivity(intent);
        });
        Picasso.with(context)
                .load(posterPath)
                .into(holder.imageView);
    }
    public void addData(List<ResultMovie> resultMovies) {
        int totalItem = getItemCount();
        resultMovieList.addAll(resultMovies);
        notifyItemRangeInserted(totalItem, getResultMovieList() - 1);

    }
    public void setData(List<ResultMovie> resultMovies) { resultMovieList = resultMovies; }
    @Override
    public int getItemCount() {
        return resultMovieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewDetailed);
            title = itemView.findViewById(R.id.title);
        }
    }
}
