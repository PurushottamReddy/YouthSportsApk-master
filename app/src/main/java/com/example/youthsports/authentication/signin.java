package com.example.youthsports.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youthsports.R;
import com.example.youthsports.dashboards.HomeScreen;
import com.example.youthsports.network.JwtTokenService;
import com.example.youthsports.network.RetrofitClient;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.model.UserLogin;
import com.example.youthsports.util.UserDetailsInSharedPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class signin extends AppCompatActivity {

    private static final String TAG = "SignInActivity";


    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton signInButton,signUpButton,resetPasswordButton;
    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout;

    private JwtTokenService jwtTokenService;


    public signin(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        UserDetailsInSharedPreferences.initialize(getApplicationContext());
        jwtTokenService=new JwtTokenService(getApplicationContext());

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);

        signInButton.setOnClickListener(this::attemptSignIn);
        signUpButton.setOnClickListener(this::moveToSignup);
        resetPasswordButton.setOnClickListener(this::moveToRequestPassword);
    }

    private void moveToSignup(View view){
        Intent intent = new Intent(signin.this, signup.class);
        startActivity(intent);
    }
    private void moveToRequestPassword(View view){
        Intent intent = new Intent(signin.this, RequestResetPassword.class);
        startActivity(intent);
    }

    private void moveToHomeScreen(View view){
        Intent intent = new Intent(signin.this, HomeScreen.class);
        startActivity(intent);
    }

    private void attemptSignIn(View view) {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            usernameTextInputLayout.setError(email.isEmpty() ? "Email cannot be empty" : null);
            passwordTextInputLayout.setError(password.isEmpty() ? "Password cannot be empty" : null);
            return;
        }

        UserLogin userLogin = new UserLogin(email, password);
        Log.d(TAG, "Attempting sign-in for user: " + userLogin.toString());


        RetrofitClient.getApiService(getApplicationContext()).signIn(userLogin).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Sign-in API call successful");
                    Log.d(TAG, "Response message: " + apiResponse.getMessage());
                    Snackbar.make(view, apiResponse.getMessage(), Snackbar.LENGTH_LONG).show();
                    // Read Authorization header from response
                    String authorizationHeader = response.headers().get("Authorization");
                    Log.d(TAG,"Authorization Header: "+authorizationHeader);
                    if (authorizationHeader != null) {
                        Log.d(TAG, "Authorization header value: " + authorizationHeader);
                        // Extract token from the header
                        String token = authorizationHeader.substring(7); // Assuming "Bearer " prefix
                        // Store the token
                        jwtTokenService.storeToken(token);
                        Log.d(TAG, "JWT token stored successfully");
                        Toast.makeText(signin.this, "Sign in Success!", Toast.LENGTH_SHORT).show();
                        storeUserLoginInformationInSharedPreferences();
                        moveToHomeScreen(view);
                    } else {
                        Log.d(TAG, "Authorization header not found in response");
                    }
                } else {
                    String errorMessage = "Failed to authenticate user.";
                    if (response.errorBody() != null) {
                        errorMessage = "Invalid credentials"; // Customize based on actual error response
                        try {
                            errorMessage += ": " + response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, errorMessage);
                    Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Snackbar.make(view, "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });


    }

    private void storeUserLoginInformationInSharedPreferences(){

        RetrofitClient.getApiService(getApplicationContext()).getUserInformation().enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful response
                    UserLogin userDetails = response.body();
                    // Update UI with user details (assuming you have TextViews for name, email, phone number)

                    Log.i(TAG,"account details fetched "+response.body());
                    UserDetailsInSharedPreferences.storeValue("userId",""+userDetails.getUserId());
                    UserDetailsInSharedPreferences.storeValue("name",userDetails.getName());
                    UserDetailsInSharedPreferences.storeValue("contactNumber",userDetails.getContactNumber());
                    UserDetailsInSharedPreferences.storeValue("userEmail",userDetails.getUserEmail());
                    UserDetailsInSharedPreferences.storeValue("accountType",userDetails.getAccountType().toString());


                } else {
                    Log.i(TAG,"Unable to fetch user details "+response);
                    // Handle unsuccessful response
                    Toast.makeText(getApplicationContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                // Handle network failure
                Toast.makeText(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
