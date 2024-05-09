package com.example.youthsports.authentication;

import android.content.Intent;
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

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestResetPassword extends AppCompatActivity {

    private EditText emailEditText;
    private MaterialButton requestOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reset_password);

        emailEditText = findViewById(R.id.emailEditText);
        requestOtpButton = findViewById(R.id.requestOtpButton);

        requestOtpButton.setOnClickListener(view -> requestResetPassword(view));
    }

    private void moveToResetPassword(View view,String userEmail){
        Intent intent = new Intent(RequestResetPassword.this, ResetPassword.class);
        intent.putExtra("userEmail",userEmail);
        startActivity(intent);
    }

    private void requestResetPassword(View view) {
        String userEmail = emailEditText.getText().toString().trim();
        if (userEmail.isEmpty()) {
            Snackbar.make(view, "Email cannot be empty", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Network call to request reset password OTP
        RetrofitClient.getApiService(getApplicationContext()).requestPasswordReset(userEmail).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Snackbar.make(view, "OTP sent to your email", Snackbar.LENGTH_LONG).show();
                    moveToResetPassword(view, userEmail); // Pass the email to the next activity
                } else {
                    Snackbar.make(view, "Failed to send OTP", Snackbar.LENGTH_LONG).show();

                    // Log the response code and error body if available
                    if (response.errorBody() != null) {
                        try {
                            Log.d("RequestResetPassword", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("RequestResetPassword", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Snackbar.make(view, "Network error", Snackbar.LENGTH_LONG).show();

                // Log the error message
                Log.e("RequestResetPassword", "Network error", t);
            }
        });
    }


}
