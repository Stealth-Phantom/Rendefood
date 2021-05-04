package eng.asu.rendefood.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import eng.asu.rendefood.Model.Review;
import eng.asu.rendefood.R;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {
    private final LayoutInflater mInflater;
    private List<Review> reviews; // Cached copy of reviews
    Intent intent;

    ReviewListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.review_recyclerview_item, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        if (reviews != null) { //Set data of current review in the UI
            Review current = reviews.get(position);
            holder.reviewerNameText.setText(current.getUsername());
            holder.reviewerOpinionText.setText(current.getOpinion());
            holder.rating.setRating(current.getScore());

        } else {
            // Covers the case of data not being ready yet.
            holder.reviewerNameText.setText("Creepy User");
            holder.reviewerOpinionText.setText("Creepy Opinion");
            holder.rating.setRating(0);
        }
    }

    void setReviews(List<Review> reviewList){
        reviews = reviewList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (reviews != null)
            return reviews.size();
        else return 0;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {//Should have all fields of the recycler view item (Should be final)
        private final TextView reviewerNameText;
        private final TextView reviewerOpinionText;
        private final RatingBar rating;
        private ReviewViewHolder(View itemView) { //Link variables to their respective fields
            super(itemView);
            reviewerNameText = itemView.findViewById(R.id.reviewerName);
            reviewerOpinionText = itemView.findViewById(R.id.reviewerOpinion);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
