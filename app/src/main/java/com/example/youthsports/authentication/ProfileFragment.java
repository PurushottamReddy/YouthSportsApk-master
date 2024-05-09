package com.example.youthsports.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youthsports.R;
import com.example.youthsports.model.UserLogin;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.network.JwtTokenService;
import com.example.youthsports.network.RetrofitClient;
import com.example.youthsports.util.UserDetailsInSharedPreferences;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private String TAG ="ProfileFragment";

    private ImageView logout,profileImageView;
    private TextView profileName,profilePhoneNumber,profileEmail,logoutTextview;

    private Button saveChangesButton;

    private JwtTokenService jwtTokenService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.profile_fragment, container, false);
        jwtTokenService = new JwtTokenService(getContext());
        findByIds(view);
        getUserDetails();
        setOnClickListeners();
        return view;
    }


    private void findByIds(View view){
        logout=view.findViewById(R.id.Profile_Fragment_ImgView_Logout_Icon);
        logoutTextview = view.findViewById(R.id.Profile_Fragment_TxtView_Logout_txt);
        profileName =view.findViewById(R.id.userNameEdtTxt);
        profileEmail = view.findViewById(R.id.userEmailTxtView);
        profileImageView =view.findViewById(R.id.profileImageView);
        profilePhoneNumber = view.findViewById(R.id.contactEditText);
        saveChangesButton = view.findViewById(R.id.saveButton);

        String accountType = UserDetailsInSharedPreferences.getValue("accountType");
        if(accountType!=null && !accountType.isEmpty() && (accountType.contentEquals("Coach"))){

            Log.i(TAG,"Icon for account type: "+accountType);
            if(accountType.contentEquals("Coach"))
                profileImageView.setImageResource(R.drawable.coach_icon);
            else if(accountType.contentEquals("Player"))
                profileImageView.setImageResource(R.drawable.player_icon);
            else
                profileImageView.setImageResource(R.drawable.parent_icon);

        }
        else
            Log.i(TAG,"Account type is null "+accountType);
    }

    private void setOnClickListeners(){
        logout.setOnClickListener(logoutListener());
        logoutTextview.setOnClickListener(logoutListener());

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails(profileName.getText().toString(), profilePhoneNumber.getText().toString());
            }
        });
    }

    private View.OnClickListener logoutListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Logout Success!",Toast.LENGTH_LONG).show();
                jwtTokenService.removeToken();
                Intent intent=new Intent(getContext(), signin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };
    }

    private void getUserDetails(){
        RetrofitClient.getApiService(getContext()).getUserInformation().enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful response
                    UserLogin userDetails = response.body();
                    // Update UI with user details (assuming you have TextViews for name, email, phone number)
                    profileName.setText(userDetails.getName());
                    profilePhoneNumber.setText(userDetails.getContactNumber());
                    profileEmail.setText(userDetails.getUserEmail());
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                // Handle network failure
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUserDetails(String name, String contactNumber) {
        UserLogin updatedUser = new UserLogin();
        updatedUser.setName(name);
        updatedUser.setContactNumber(contactNumber);

        RetrofitClient.getApiService(getContext()).updateUserDetails(updatedUser).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(getContext(), "User details updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update user details: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to update user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}