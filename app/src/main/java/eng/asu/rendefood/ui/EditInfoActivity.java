package eng.asu.rendefood.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eng.asu.rendefood.Model.User;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;
import eng.asu.rendefood.databinding.ActivityEditInfoBinding;

public class EditInfoActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "UserData";
    ActivityEditInfoBinding binding;
    ReservationViewModel reservationViewModel;
    Intent intent;
    FirebaseAuth mAuth;
    FirebaseUser user;
    User normalUser;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditInfoBinding.inflate(getLayoutInflater());
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
        getSupportActionBar().setTitle("Edit User Info");

        settings = getSharedPreferences(PREFS_NAME,0);

        intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        reservationViewModel = new ViewModelProvider(this,new ReservationViewModelFactory(getApplication(),user.getEmail())).get(ReservationViewModel.class);

        binding.fullNameEdit.setText(settings.getString("Username","Ha"));
        binding.phoneEdit.setText(settings.getString("Userphone","382"));
        binding.submitEditButton.setOnClickListener(submitListener);
    }

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name = binding.fullNameEdit.getText().toString().trim();
            String oldPassword = binding.oldPassword.getText().toString().trim();
            String newPassword = binding.newPasswordEdit.getText().toString().trim();
            String confirmNewPassword = binding.confirmNewPassword.getText().toString().trim();
            String phone = binding.phoneEdit.getText().toString().trim();
            normalUser = reservationViewModel.getUser();

            boolean invalid = false;

            if(name.isEmpty()) {
                binding.fullNameEdit.setError("Please enter your name");
                invalid = true;
            }
            if(oldPassword.isEmpty()) {
                binding.oldPassword.setError("Please enter password to edit info");
                invalid = true;
            }
            try {
                if (!oldPassword.equals(normalUser.getPassword())) {
                    binding.oldPassword.setError("Incorrect password");
                    invalid = true;
                }
            }catch (Exception e){}
            if(phone.isEmpty()) {
                binding.phoneEdit.setError("Please enter your phone");
                invalid = true;
            }

            if(!newPassword.isEmpty())
            {
                if(newPassword.length() < 6) {
                    binding.newPasswordEdit.setError("Password must be at least 6 characters");
                    invalid = true;
                }
                else if(confirmNewPassword.isEmpty())
                {
                    binding.confirmNewPassword.setError("Please confirm your password");
                    invalid = true;
                }
                else if(!newPassword.equals(confirmNewPassword))
                {
                    binding.confirmNewPassword.setError("New password is not correct");
                    invalid = true;
                }
            }

            if(!invalid)
            {
                boolean passwordNew = false;
                if(!newPassword.isEmpty())
                {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), normalUser.getPassword());
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Log.d("Reauthentication", "User re-authenticated.");
                                        user.updatePassword(newPassword);
                                        normalUser.setPassword(newPassword);
                                        normalUser.setName(name);
                                        normalUser.setPhone(phone);

                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("Username", normalUser.getName());
                                        editor.putString("Userphone", normalUser.getPhone());
                                        editor.commit();

                                        reservationViewModel.updateUser(user.getEmail(),normalUser);

                                        Toast.makeText(EditInfoActivity.this,"Info Edited Successfully!",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(EditInfoActivity.this,"No internet Connection!",Toast.LENGTH_LONG).show();
                                        Log.d("Reauthentication", "User re-authentication Failed.");
                                    }
                                }
                            });

                }
                else{
                    normalUser.setName(name);
                    normalUser.setPhone(phone);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Username", normalUser.getName());
                    editor.putString("Userphone", normalUser.getPhone());
                    editor.commit();

                    reservationViewModel.updateUser(user.getEmail(),normalUser);

                    Toast.makeText(EditInfoActivity.this,"Info Edited Successfully!",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    };
}