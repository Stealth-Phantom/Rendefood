package eng.asu.rendefood.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.Locale;

import eng.asu.rendefood.Model.Reservation;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;
import eng.asu.rendefood.databinding.ActivityReservationBinding;
import eng.asu.rendefood.databinding.ActivityReservationInfoBinding;

public class ReservationInfoActivity extends AppCompatActivity {

    ActivityReservationInfoBinding binding;
    Reservation reservation;
    Intent intent;
    ReservationViewModel reservationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationInfoBinding.inflate(getLayoutInflater());
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
        getSupportActionBar().setTitle("Reservation Info");

        intent = getIntent();
        reservation = (Reservation) intent.getSerializableExtra("reservation");
        binding.reservedByName.append(" "+ reservation.getReservedByEmail());
        binding.reservedDate.setText(reservation.getDate());
        binding.reservedTime.setText(reservation.getTime()+":00");
        binding.reservedNumber.append(" "+reservation.getReservations());
        binding.cancelReservation.setOnClickListener(cancelListener);
        binding.completeReservation.setOnClickListener(completeListener);

        if(getMinsDiff() > 30)
            binding.completeReservation.setEnabled(false);

        reservationViewModel = new ViewModelProvider(this,new ReservationViewModelFactory(getApplication(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail())).get(ReservationViewModel.class);

    }

    View.OnClickListener completeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            reservation.setStatus("Complete");
            reservationViewModel.updateReservation(reservation);
            finish();
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            int minsDiff = getMinsDiff();

            if(currentDate.compareTo(reservation.getDate())==0 && minsDiff > 0)
            {
                if(minsDiff <= 60)
                {
                    reservation.setStatus("Cancelled");
                    reservationViewModel.updateReservation(reservation);
                    finish();
                }
                else{
                    reservationViewModel.deleteReservation(reservation);
                    finish();
                }
            }
            else{
                reservationViewModel.deleteReservation(reservation);
                finish();
            }
        }
    };
    public int getMinsDiff()
    {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        if(currentDate.compareTo(reservation.getDate())!=0)
            return 999;
        int currentMins,currentHour,registeredMin,registeredHour,minsDiff;
        currentHour = Integer.parseInt(currentTime.substring(0,currentTime.indexOf(":")));
        currentMins = Integer.parseInt(currentTime.substring(currentTime.indexOf(":")+1));
        registeredHour = reservation.getTime();
        registeredMin = 0;

        minsDiff = (registeredHour-currentHour)*60+(Math.abs(currentMins-registeredMin));
        return minsDiff;
    }
}