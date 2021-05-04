package eng.asu.rendefood.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.Locale;

import eng.asu.rendefood.Model.Reservation;
import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.Model.Review;
import eng.asu.rendefood.Model.Slot;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;
import eng.asu.rendefood.databinding.ActivityReservationBinding;

public class ReservationActivity extends AppCompatActivity {

    ActivityReservationBinding binding;
    String date = "", chosenTime="";
    int time = 0;
    Intent intent;
    Restaurant restaurant;
    ReservationViewModel reservationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
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
        getSupportActionBar().setTitle("Reservation");

        intent = getIntent();
        restaurant = (Restaurant) intent.getSerializableExtra("restaurant");
        binding.reservationNumberPicker.setMinValue(1);
        binding.reservationNumberPicker.setMaxValue(8);
        binding.reservationNumberPicker.setValue(4);
        binding.datePickerText.setOnClickListener(pickDateListener);
        binding.timePickerText.setOnClickListener(pickTimeListener);
        binding.submitButton.setOnClickListener(submitListener);

        reservationViewModel = new ViewModelProvider(this,new ReservationViewModelFactory(getApplication(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail())).get(ReservationViewModel.class);
    }

    View.OnClickListener pickTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Dialog d = new Dialog(ReservationActivity.this);
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.dialog);
            Button b1 = (Button) d.findViewById(R.id.button4);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(restaurant.getCloseTime() - 1);
            np.setMinValue(restaurant.getOpenTime());
            np.setWrapSelectorWheel(false);
            b1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    time = np.getValue();
                    chosenTime = ""+time;
                    if(time/10==0)
                        chosenTime = "0"+time;
                    chosenTime = chosenTime+":00";
                    binding.timePickerText.setText(chosenTime);
                    d.dismiss();
                }
            });
            b2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
        }
    };

    View.OnClickListener pickDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //To show current date in the datepicker
            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    selectedmonth = selectedmonth + 1;
                    String d=""+selectedday,m=""+selectedmonth,y=""+selectedyear;
                    if(selectedday/10 == 0)
                        d = "0" + selectedday;
                    if(selectedmonth/10==0)
                        m = "0" + selectedmonth;
                    binding.datePickerText.setText(d + "/" + m + "/" + y);
                    date = d + "/" + m + "/" + y;
                }
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis() + 86400000*6);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        }
    };

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int opening = restaurant.getOpenTime();
            int closing = restaurant.getCloseTime();
            if(date.isEmpty())
                binding.datePickerText.setError("Please choose a date");
            else if(chosenTime.isEmpty())
                binding.timePickerText.setError("Please choose a time");
            else if(opening > closing)
                Toast.makeText(ReservationActivity.this,"Please choose a future time",Toast.LENGTH_LONG).show();
            else{
                int seats = binding.reservationNumberPicker.getValue();
                Reservation reservation = new Reservation(date,time,restaurant.getName(),"Incomplete",seats,
                        FirebaseAuth.getInstance().getCurrentUser().getEmail());
                reservationViewModel.insert(reservation);
                finish();
            }
        }
    };


}