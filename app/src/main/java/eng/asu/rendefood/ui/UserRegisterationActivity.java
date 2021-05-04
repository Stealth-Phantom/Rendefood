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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import eng.asu.rendefood.Model.User;
import eng.asu.rendefood.R;
import eng.asu.rendefood.ViewModel.ReservationViewModel;
import eng.asu.rendefood.ViewModel.ReservationViewModelFactory;
import eng.asu.rendefood.databinding.ActivityUserRegisterationBinding;

public class UserRegisterationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityUserRegisterationBinding binding;
    private ReservationViewModel reservationViewModel;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserRegisterationBinding.inflate(getLayoutInflater());
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
        getSupportActionBar().setTitle("Registeration");

        mAuth = FirebaseAuth.getInstance();
        intent = new Intent(UserRegisterationActivity.this,MainActivity.class);
        binding.registerButton.setOnClickListener(registerListener);

    }

    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = binding.fullNamePrompt.getText().toString().trim();
            String password = binding.passwordRegisterPrompt.getText().toString();
            String confirmPassword = binding.confirmPassword.getText().toString();
            String phone = binding.phonePrompt.getText().toString().trim();
            String email = binding.emailPrompt.getText().toString().trim().toLowerCase();

            boolean invalid = false;

            if(name.isEmpty()) {
                binding.fullNamePrompt.setError("Please enter your name");
                invalid = true;
            }
            if(password.length() < 6) {
                binding.passwordRegisterPrompt.setError("Password must be at least 6 characters");
                invalid = true;
            }
            if(password.isEmpty()) {
                binding.passwordRegisterPrompt.setError("Please enter password");
                invalid = true;
            }
            if(confirmPassword.isEmpty()) {
                binding.confirmPassword.setError("Please confirm the password");
                invalid = true;
            }
            if(phone.isEmpty()) {
                binding.phonePrompt.setError("Please enter your phone");
                invalid = true;
            }
            if(email.isEmpty()) {
                binding.emailPrompt.setError("Please enter your email");
                invalid = true;
            }
            if(!password.equals(confirmPassword)) {
                binding.confirmPassword.setError("Password is incorrect");
                invalid = true;
            }

            if(!invalid)
            {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(UserRegisterationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign UP success, update UI with the signed-in user's information
                                    Log.d("Firebase Registeration", "createUserWithEmail:success");
                                    reservationViewModel = new ViewModelProvider(UserRegisterationActivity.this,new ReservationViewModelFactory(getApplication(),email)).get(ReservationViewModel.class);
                                    reservationViewModel.createUser(new User(email,name,password,phone));
                                    Toast.makeText(UserRegisterationActivity.this, "Successfully Registered!", Toast.LENGTH_LONG).show();
                                    SharedPreferences settings = getSharedPreferences ("UserData",0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("Username", name);
                                    editor.putString("Userphone", phone);
                                    editor.commit();
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Firebase Registeration", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(UserRegisterationActivity.this, "Authentication failed, check your internet, else email might have been used for another account",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
    };
}