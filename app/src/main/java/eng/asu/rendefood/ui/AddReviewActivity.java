package eng.asu.rendefood.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.Model.Review;
import eng.asu.rendefood.Model.User;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;
import eng.asu.rendefood.ViewModel.RestaurantViewModel;
import eng.asu.rendefood.databinding.ActivityAddReviewBinding;

public class AddReviewActivity extends AppCompatActivity {

    FirebaseUser user;
    SharedPreferences settings;
    ActivityAddReviewBinding binding;
    Intent intent;
    RestaurantViewModel restaurantViewModel;
    ReservationViewModel reservationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        intent = getIntent();

        user = FirebaseAuth.getInstance().getCurrentUser();

        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle("Add Review");

        binding.submitReview.setOnClickListener(addReviewListener);
        settings = getSharedPreferences("UserData",0);
        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);

        if(settings.getString("Username","").isEmpty())
        {
            reservationViewModel = new ViewModelProvider(this,new ReservationViewModelFactory(getApplication(),user.getEmail())).get(ReservationViewModel.class);
        }

    }

    View.OnClickListener addReviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(settings.getString("Username","").isEmpty())
            {
                User normalUser = reservationViewModel.getUser();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Username", normalUser.getName());
                editor.putString("Userphone", normalUser.getPhone());
                editor.commit();
            }
            String username = settings.getString("Username","");
            float score = binding.ratingBar.getRating();
            String opinion = binding.opinion.getText().toString().trim();
            Restaurant restaurant = (Restaurant)intent.getSerializableExtra("restaurant");
            if(score == 0)
                Toast.makeText(view.getContext(),"Please insert A score",Toast.LENGTH_SHORT).show();
            else {
                Review review = new Review(username, opinion, score, restaurant.getName());
                restaurantViewModel.insert(review, restaurant);
                Toast.makeText(view.getContext(), "Review Added Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };
}