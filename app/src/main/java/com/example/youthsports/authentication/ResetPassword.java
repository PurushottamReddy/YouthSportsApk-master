package com.example.youthsports.authentication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youthsports.R;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    private EditText otpEditText;
    private EditText newPasswordEditText;
    private MaterialButton resetPasswordButton;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize userEmail here
        userEmail = getIntent().getStringExtra("userEmail");

        otpEditText = findViewById(R.id.otpEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetPasswordButton.setOnClickListener(view -> resetPassword(view));
    }

    private void resetPassword(View view) {
        String otp = otpEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        if (otp.isEmpty() || newPassword.isEmpty()) {
            Snackbar.make(view, "OTP and New Password cannot be empty", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Network call to reset password
        RetrofitClient.getApiService(getApplicationContext()).resetPassword(userEmail, otp, newPassword)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            Log.d(TAG, "Password reset successfully: " + apiResponse.getMessage());
                            Snackbar.make(view, "Password reset successfully", Snackbar.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "Failed to reset password");
                            Snackbar.make(view, "Failed to reset password", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        Snackbar.make(view, "Network error", Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
