package eng.asu.rendefood.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eng.asu.rendefood.R;
import eng.asu.rendefood.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityLoginBinding binding;
    Intent loginIntent;
    Intent registerationIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
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
        getSupportActionBar().setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        binding.signInButton.setOnClickListener(signInListener);
        binding.register.setOnClickListener(registerListener);

        registerationIntent = new Intent(LoginActivity.this, UserRegisterationActivity.class);
        loginIntent = new Intent(LoginActivity.this, MainActivity.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(Network network) {
                    startActivity(loginIntent);
                    finish();
                }
                @Override
                public void onLost(Network network) {

                }
            });
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if(capabilities==null){Toast.makeText(getApplication(), "No internet connection available!", Toast.LENGTH_SHORT).show();}
        }
    }

    View.OnClickListener signInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("FIREBASE LOGIN", "ENTERED ON CLICK");
            String email = binding.emailPrompt.getText().toString().trim().toLowerCase();
            String password = binding.passwordPrompt.getText().toString().trim();
            if(email.isEmpty())
                binding.emailPrompt.setError("Please insert email");
            else if(password.isEmpty())
                binding.passwordPrompt.setError("Please insert password");
            else if(email.substring(email.indexOf("@")+1).equals("shinra.com"))
                binding.emailPrompt.setError("Please enter a user email");
            else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("FIREBASE LOGIN", "signInWithEmail:success");
                                    startActivity(loginIntent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("FIREBASE LOGIN", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed. Make sure of email/password and internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    };

    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(registerationIntent);
            finish();
        }
    };
}