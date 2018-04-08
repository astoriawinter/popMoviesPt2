package com.astoria.movieapp.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astoria.movieapp.R;
import com.astoria.movieapp.model.ResultReview;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    private List<ResultReview> resultReviewList = new ArrayList<>();
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        final ResultReview resultReview = resultReviewList.get(position);
        final String reviewText = resultReview.getContent();
        holder.reviewText.setText(reviewText);
    }
    public void setData(List<ResultReview> resultReviews) {
        resultReviewList = resultReviews;
        notifyItemRangeInserted(0, resultReviewList.size());
    }
    @Override
    public int getItemCount() {
        return resultReviewList == null ? 0 : resultReviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView reviewText;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.reviewText);
        }
    }
}
