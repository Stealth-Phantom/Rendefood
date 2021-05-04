package eng.asu.rendefood.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import eng.asu.rendefood.Model.Reservation;
import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;

public class CurrentReservationsActivity extends AppCompatActivity {
    LinearLayoutManager layoutManager;
    ReservationViewModel reservationViewModel;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_reservations);

        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle("Current Reservations");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        reservationViewModel = new ViewModelProvider(this,new ReservationViewModelFactory(getApplication(),user.getEmail())).get(ReservationViewModel.class);

        //RecyclerView Setup
        layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.currentReservationsRecyclerview);
        final ReservationListAdapter adapter = new ReservationListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        reservationViewModel.getUnfinishedReservations().observe(this, new Observer<List<Reservation>>() {
            @Override
            public void onChanged(@Nullable final List<Reservation> reservations){
                // Update the cached copy of the words in the adapter.
                adapter.setReservations(reservations);
            }
        });
    }


}