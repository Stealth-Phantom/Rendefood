package eng.asu.rendefood.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eng.asu.rendefood.Model.User;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;
import eng.asu.rendefood.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "UserData";

    ActivityProfileBinding binding;
    Intent editInfoIntent;
    Intent historyIntent;
    ReservationViewModel reservationViewModel;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        editInfoIntent = new Intent(this,EditInfoActivity.class);
        historyIntent = new Intent(this,HistoryActivity.class);

        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        reservationViewModel = new ViewModelProvider(this,new ReservationViewModelFactory(getApplication(),user.getEmail())).get(ReservationViewModel.class);

        binding.signOut.setOnClickListener(signOutListener);
        binding.changeInfo.setOnClickListener(editInfoListener);
        binding.reservationHistory.setOnClickListener(historyListener);
    }

    View.OnClickListener signOutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(ProfileActivity.this, "Signed out! See you soon!", Toast.LENGTH_LONG).show();
            Intent intents = new Intent(ProfileActivity.this, LoginActivity.class);
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mAuth.signOut();
            SharedPreferences settings = getSharedPreferences (PREFS_NAME,0);
            SharedPreferences.Editor editor = settings.edit();
                editor.putString("Username", "");
                editor.putString("Userphone", "");
                editor.commit();
            startActivity(intents);
            finish();
        }
    };

    View.OnClickListener editInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            User normalUser = reservationViewModel.getUser();
            SharedPreferences settings = getSharedPreferences (PREFS_NAME,0);
            SharedPreferences.Editor editor = settings.edit();
            if(settings.getString("Username","").isEmpty()) {
                editor.putString("Username", normalUser.getName());
                editor.putString("Userphone", normalUser.getPhone());
                editor.commit();
            }
            startActivity(editInfoIntent);
        }
    };

    View.OnClickListener historyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(historyIntent);
        }
    };

}