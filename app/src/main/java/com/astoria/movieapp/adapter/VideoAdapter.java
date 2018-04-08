package com.astoria.movieapp.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astoria.movieapp.R;
import com.astoria.movieapp.model.ResultReview;
import com.astoria.movieapp.model.ResultVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ReviewViewHolder>{
    private List<ResultVideo> resultVideoList = new ArrayList<>();
    private final Context context;
    private String key;
    private final List<Uri> videoUri = new ArrayList<>();
    public VideoAdapter(Context context) {
        this.context = context;
    }
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        final ResultVideo resultVideo = resultVideoList.get(position);
        holder.image.setTag(position);
        String thumbnail_URL = "https://img.youtube.com/vi/";
        String thumbnailNumber = "/0.jpg";
        key = resultVideo.getKey();
        videoUri.add(Uri.parse("https://www.youtube.com/watch?v=" + key));
        final String video = thumbnail_URL + key + thumbnailNumber;
        Picasso.with(context)
                .load(video)
                .into(holder.image);
    }
    public void setData(List<ResultVideo> videoReviews) {
        resultVideoList = videoReviews;
        notifyItemRangeInserted(0, resultVideoList.size());
    }
    @Override
    public int getItemCount() {
        return resultVideoList == null ? 0 : resultVideoList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.trailerImage);
            image.setOnClickListener(view -> {
                int position = (int)view.getTag();
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, videoUri.get(position)));
            });
        }
    }
}
