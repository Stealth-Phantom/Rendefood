package eng.asu.rendefood.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eng.asu.rendefood.Model.MenuItem;
import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.Model.Review;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.RestaurantViewModel;
import eng.asu.rendefood.databinding.ActivityRestaurantBinding;
import eng.asu.rendefood.databinding.ActivityReviewBinding;

public class ReviewActivity extends AppCompatActivity {

    Intent intent;
    ActivityReviewBinding binding;
    RestaurantViewModel restaurantViewModel;
    String restaurantName;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle("Reviews");

        intent = getIntent();
        restaurantName = intent.getStringExtra("name");

        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);

        layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = findViewById(R.id.reviewRecyclerview);
        final ReviewListAdapter adapter = new ReviewListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        restaurantViewModel.getReviewsForRestaurant(restaurantName).observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable final List<Review> reviews){
                // Update the cached copy of the words in the adapter.
                adapter.setReviews(reviews);
            }
        });
    }
}