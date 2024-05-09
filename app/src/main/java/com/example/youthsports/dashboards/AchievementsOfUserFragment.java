package com.example.youthsports.dashboards;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.youthsports.R;
import com.example.youthsports.model.AchievementModel;
import com.example.youthsports.model.UserLogin;
import com.example.youthsports.network.ApiService;
import com.example.youthsports.network.RetrofitClient;
import com.example.youthsports.util.UserDetailsInSharedPreferences;
import com.example.youthsports.util.YouthSportsUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementsOfUserFragment extends Fragment {

    private RecyclerView achievementRecyclerView;
    private AchievementAdapter achievementAdapter;

    private FloatingActionButton achievementFab;

    private String TAG ="AchievementsOfUserFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.achievements_user_fragment, container, false);
        findViewByIds(view);
        achievementRecyclerView = view.findViewById(R.id.rvAchievements);
        achievementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Default orientation is vertical

        achievementAdapter = new AchievementAdapter(getContext());
        achievementRecyclerView.setAdapter(achievementAdapter);

        fetchAchievementsOfUser();
        return view;
    }
    private void findViewByIds(View view){
        achievementFab = view.findViewById(R.id.achievementfab);
        String accountType = UserDetailsInSharedPreferences.getValue("accountType");
        if(accountType!=null && !accountType.isEmpty() && (accountType.contentEquals("Coach"))){
            achievementFab.setVisibility(View.VISIBLE);
            Log.i(TAG,"Fab is visible "+accountType);
        }
        else
            Log.i(TAG,"Fab is invisible "+accountType);

        achievementFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             showAddAchievementDialog();
            }
        });

    }
    public void showAddAchievementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.create_achievement_dialog, null);
        builder.setView(view);
        builder.setTitle("Add Achievement!");
        TextInputEditText titleEditText = view.findViewById(R.id.edit_text_title);
        TextInputEditText descriptionEditText = view.findViewById(R.id.edit_text_description);
        MaterialButton selectDateButton = view.findViewById(R.id.button_select_date);

        // DatePicker Dialog
        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (datePicker, year1, month1, dayOfMonth) -> {
                        // Handle the date chosen by the user
                        selectDateButton.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String date = selectDateButton.getText().toString();
            // Here you can call the method to handle the data
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date outputdate = new Date();
            try {
                outputdate = inputFormat.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Log.i(TAG, "showAddAchievementDialog: "+date);

            createAchievement(title,description,outputdate);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createAchievement(String title, String description, Date date){
        Log.i(TAG,"Create Achievement: "+date);
        Long userId = Long.parseLong(UserDetailsInSharedPreferences.getValue("userId"));

        UserLogin achievedUser = new UserLogin(userId);
        AchievementModel achievementModel = new AchievementModel(title,description,date,achievedUser);
        ApiService apiService = RetrofitClient.getApiService(getContext());
        apiService.createAnAchievement(achievementModel).enqueue(new Callback<AchievementModel>() {
            @Override
            public void onResponse(Call<AchievementModel> call, Response<AchievementModel> response) {
                if(response!=null && response.body()!=null){
                    YouthSportsUtil.showSnackbar(getView(),"Achievement Added Successfully!");
                    fetchAchievementsOfUser();
                }else{
                    YouthSportsUtil.showSnackbar(getView(),"Unable to add achievement!");
                }
            }

            @Override
            public void onFailure(Call<AchievementModel> call, Throwable t) {
                Log.i(TAG,"Network failure to create achievement!");
            }
        });
    }

    private void fetchAchievementsOfUser() {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<List<AchievementModel>> call = apiService.getAllAchievements();

        call.enqueue(new Callback<List<AchievementModel>>() {
            @Override
            public void onResponse(Call<List<AchievementModel>> call, Response<List<AchievementModel>> response) {
                if (response!=null && response.body()!=null) {
                    Log.i(TAG,"Fetched Achievements: ");
                    achievementAdapter.setAchievements(response.body());
                }else{
                    Log.i(TAG,"Unable to fetch achievements "+response);
                }
            }

            @Override
            public void onFailure(Call<List<AchievementModel>> call, Throwable t) {
               Log.i(TAG,"Network failure to fetch achievements");
            }
        });
    }
}

class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<AchievementModel> achievements = new ArrayList<>();
    private LayoutInflater inflater;

    public AchievementAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AchievementModel achievement = achievements.get(position);
        holder.title.setText(achievement.getTitle());
        holder.description.setText(achievement.getDescription());
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public void setAchievements(List<AchievementModel> achievements) {
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            description = itemView.findViewById(R.id.txtDescription);
        }
    }
}