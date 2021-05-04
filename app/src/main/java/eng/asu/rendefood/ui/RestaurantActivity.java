package eng.asu.rendefood.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import eng.asu.rendefood.Model.MenuItem;
import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.RestaurantViewModel;
import eng.asu.rendefood.databinding.ActivityRestaurantBinding;

public class RestaurantActivity extends AppCompatActivity {

    Intent receivedIntent;
    Intent reviewsIntent;
    Intent reserveIntent;
    Intent addReviewIntent;
    Restaurant restaurant;
    RestaurantViewModel restaurantViewModel;
    ActivityRestaurantBinding binding;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        receivedIntent = getIntent();
        reviewsIntent = new Intent(this, ReviewActivity.class);
        reserveIntent = new Intent(this,ReservationActivity.class);
        addReviewIntent = new Intent(this, AddReviewActivity.class);
        restaurant = (Restaurant)receivedIntent.getSerializableExtra("restaurant");

        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle(restaurant.getName());

        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);

        //List<Review> reviews = restaurantViewModel.getReviewsForRestaurant(restaurant.getName());
        //List<MenuItem> menu = restaurantViewModel.getMenuForRestaurant(restaurant.getName());

        binding.restaurantName.setText(restaurant.getName());
        binding.restaurantCategory.setText(restaurant.getCategory());
        binding.restaurantLocation.setText(restaurant.getLocation());
        binding.restaurantLocation.setPaintFlags(binding.restaurantLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.restaurantOpeningTimes.setText(restaurant.getOpenTime() + ":00 - " + restaurant.getCloseTime() + ":00");
        binding.restaurantScoreText.append(" " + restaurant.getTotalScore());

        binding.restaurantReviews.setOnClickListener(reviewListener);
        binding.reserveButton.setOnClickListener(reserveListener);
        binding.addReviewButton.setOnClickListener(addReviewListener);
        binding.restaurantLocation.setOnClickListener(locationListener);

        layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = findViewById(R.id.menuRecyclerview);
        final MenuListAdapter adapter = new MenuListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        restaurantViewModel.getMenuForRestaurant(restaurant.getName()).observe(this, new Observer<List<MenuItem>>() {
            @Override
            public void onChanged(@Nullable final List<MenuItem> menu){
                // Update the cached copy of the words in the adapter.
                adapter.setMenu(menu);
            }
        });

    }

    View.OnClickListener locationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(binding.restaurantLocation.getText().toString()));
            startActivity(i);
        }
    };

    View.OnClickListener reviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            reviewsIntent.putExtra("name",restaurant.getName());
            startActivity(reviewsIntent);
        }
    };

    View.OnClickListener reserveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            reserveIntent.putExtra("restaurant",restaurant);
            startActivity(reserveIntent);
        }
    };

    View.OnClickListener addReviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addReviewIntent.putExtra("restaurant",restaurant);
            startActivity(addReviewIntent);
        }
    };
}