package eng.asu.rendefood.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;
import eng.asu.rendefood.ViewModel.RestaurantViewModel;
import eng.asu.rendefood.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private RestaurantViewModel restaurantViewModel;
    private ReservationViewModel reservationViewModel;
    Intent profileIntent;
    Intent currentReservationsIntent;
    ActivityMainBinding binding;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        profileIntent = new Intent(MainActivity.this,ProfileActivity.class);
        currentReservationsIntent = new Intent(MainActivity.this, CurrentReservationsActivity.class);
        binding.currentReservations.setOnClickListener(currentReservationsListener);

        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences settings = getSharedPreferences ("UserData",0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Username", "");
                editor.putString("Userphone", "");
                editor.commit();
                Toast.makeText(MainActivity.this, "Signed out! See you soon!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //RecyclerView Setup
        layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.restaurantRecyclerview);
        final RestaurantListAdapter adapter = new RestaurantListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);


        reservationViewModel = new ViewModelProvider(this,new ReservationViewModelFactory(getApplication(), FirebaseAuth.getInstance().getCurrentUser().getEmail())).get(ReservationViewModel.class);
        reservationViewModel.updateReservationDB(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        restaurantViewModel.getRestaurants().observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(@Nullable final List<Restaurant> restaurants){
                // Update the cached copy of the words in the adapter.
                adapter.setRestaurants(restaurants);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        restaurantViewModel.updateDB(getApplication());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.settings:   //this item has your app icon
                startActivity(profileIntent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener currentReservationsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(currentReservationsIntent);
        }
    };
}