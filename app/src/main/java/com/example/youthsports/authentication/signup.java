package com.example.youthsports.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youthsports.R;
import com.example.youthsports.dashboards.HomeScreen;
import com.example.youthsports.model.AccountType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.example.youthsports.model.UserLogin;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.network.RetrofitClient;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class signup extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private TextInputEditText emailEditText, passwordEditText;
    private AutoCompleteTextView accountTypeAutoCompleteTextView;
    private MaterialButton signUpButton,signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        accountTypeAutoCompleteTextView = findViewById(R.id.accountTypeAutoCompleteTextView);
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
        setUpAccountTypeDropdown();

        signUpButton.setOnClickListener(view -> {
            attemptSignUp();
            closeKeyboard();
        });
        signInButton.setOnClickListener(this::moveToSignIn);
    }


    private void moveToSignIn(View view){
        Intent intent = new Intent(signup.this, signin.class);
        startActivity(intent);
    }
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setUpAccountTypeDropdown() {
        String[] accountTypes = new String[]{"Coach", "Player", "Parent"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, accountTypes);
        accountTypeAutoCompleteTextView.setAdapter(adapter);
    }

    private void attemptSignUp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String accountType = accountTypeAutoCompleteTextView.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return;
        }

        if (!isValidEmail(email)) {
            emailEditText.setError("Invalid email format");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters long");
            return;
        }

        if (accountType.isEmpty()) {
            accountTypeAutoCompleteTextView.setError("Please select an account type");
            return;
        }

        UserLogin newUser = new UserLogin();
//        newUser.setCreatedTimeStamp(new Date());
        newUser.setUserEmail(email);
        newUser.setPassword(password);
        newUser.setAccountType(AccountType.valueOf(accountType));

        signUp(newUser);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void signUp(UserLogin newUser) {
        Call<ApiResponse> signUpCall = RetrofitClient.getApiService(getApplicationContext()).signUp(newUser);
        signUpCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulSignUp(response.body());
                } else {
                    handleFailedSignUp(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void handleSuccessfulSignUp(ApiResponse apiResponse) {
        Log.d(TAG, apiResponse.getMessage());
        showSnackbar(signUpButton, apiResponse.getMessage());
        showSnackbar(signUpButton, "Verify your email");
        moveToSignIn();
    }

    private void handleFailedSignUp(Response<ApiResponse> response) {
        ApiResponse errorResponse = response.body();
        if (errorResponse != null && !errorResponse.isSuccess()) {
            showSnackbar(signUpButton, errorResponse.getMessage());
        } else {
            String errorMessage = getErrorMessageFromResponse(response);
            Log.d(TAG, errorMessage);
            showSnackbar(signUpButton, errorMessage);
        }
    }

    private String getErrorMessageFromResponse(Response<ApiResponse> response) {
        if (response.errorBody() != null) {
            try {
                // Extract error message from error body
                return "Error: " + response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error reading error message";
            }
        } else {
            return "Failed to sign up.";
        }
    }

    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error: " + t.getMessage(), t);
        showSnackbar(signUpButton, "Network error: " + t.getMessage());
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
    private void moveToSignIn(){
        Intent intent = new Intent(signup.this, signin.class);
        startActivity(intent);
    }

}
