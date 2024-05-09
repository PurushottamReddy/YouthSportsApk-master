package com.example.youthsports.dashboards;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youthsports.R;
import com.example.youthsports.model.EventModel;
import com.example.youthsports.model.EventType;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.network.ApiService;
import com.example.youthsports.network.RetrofitClient;
import com.example.youthsports.util.NotificationHelper;
import com.example.youthsports.util.UserDetailsInSharedPreferences;
import com.example.youthsports.util.YouthSportsUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private String TAG ="DashboardFragment";
    private RecyclerView rvEventsPreview, rvPracticePreview, rvSchedulePreview;
    private EventPreviewAdapter eventsAdapter, practicesAdapter, schedulesAdapter;

    private NotificationHelper notificationHelper;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private FloatingActionButton createEventFab;
    private TextView upcomingEventsTxtView,practiceScheduleTxtView,gameScheduleTxtView;
    private List<EventModel> eventsList = new ArrayList<>();
    private List<EventModel> practicesList = new ArrayList<>();
    private List<EventModel> schedulesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission granted, you can send notifications
                Log.i(TAG,"Notification permission is already given");
            } else {
                YouthSportsUtil.showSnackbar(getView(),"Please give permissions for notifications");
                Log.i(TAG,"Notifications permission is not given");
            }
        });
        notificationHelper =new NotificationHelper(getContext(),requestPermissionLauncher);

        findViewByIds(view);
        // Initialize RecyclerViews and Adapters
        rvEventsPreview = view.findViewById(R.id.rvEventsPreview);
        rvPracticePreview = view.findViewById(R.id.rvPracticePreview);
        rvSchedulePreview = view.findViewById(R.id.rvSchedulePreview);

        rvEventsPreview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPracticePreview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSchedulePreview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        eventsAdapter = new EventPreviewAdapter(getContext(), eventsList);
        practicesAdapter = new EventPreviewAdapter(getContext(), practicesList);
        schedulesAdapter = new EventPreviewAdapter(getContext(), schedulesList);

        rvEventsPreview.setAdapter(eventsAdapter);
        rvPracticePreview.setAdapter(practicesAdapter);
        rvSchedulePreview.setAdapter(schedulesAdapter);

        fetchEventsForPreview("Event", eventsList, eventsAdapter);
        fetchEventsForPreview("Practice", practicesList, practicesAdapter);
        fetchEventsForPreview("Schedule", schedulesList, schedulesAdapter);

        Log.i(TAG,"Event List Size: "+eventsList.size());
        Log.i(TAG,"Practices List Size: "+practicesList.size());
        Log.i(TAG,"Schedule List Size: "+schedulesList.size());

        return view;
    }
    private void findViewByIds(View view){
        createEventFab        = view.findViewById(R.id.createeventfab);
        String accountType = UserDetailsInSharedPreferences.getValue("accountType");
        if(accountType!=null && !accountType.isEmpty() && (accountType.contentEquals("Coach"))){
            createEventFab.setVisibility(View.VISIBLE);
            Log.i(TAG,"Fab is visible "+accountType);
        }
        else
            Log.i(TAG,"Fab is invisible "+accountType);

        upcomingEventsTxtView = view.findViewById(R.id.upcomingEventsTxtView);
        practiceScheduleTxtView = view.findViewById(R.id.practiceScheduleTxtView);
        gameScheduleTxtView = view.findViewById(R.id.gameScheduleTxtView);

        createEventFab.setOnClickListener(v -> showCreateEventDialog());
        upcomingEventsTxtView.setOnClickListener(v->showCategoryDetails("Event"));
        practiceScheduleTxtView.setOnClickListener(v->showCategoryDetails("Practice"));
        gameScheduleTxtView.setOnClickListener(v->showCategoryDetails("Schedule"));


    }


    private void showCategoryDetails(String category) {
        // Navigate to a new Fragment or Activity based on the category
        // For example, using FragmentManager for fragment transactions
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            DisplayAllIItemsInVerticalPerCategoryFragment detailsFragment = DisplayAllIItemsInVerticalPerCategoryFragment.newInstance(category);
            transaction.replace(R.id.fragment_container, detailsFragment); // Assuming you have a container in your layout
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void fetchEventsForPreview(String type, List<EventModel> list, EventPreviewAdapter adapter) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Log.i(TAG, "Fetching " + type + " events for preview");
        int page = 0;
        int limit = 3;

        apiService.getEvents(type, limit, page).enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear(); // Clear the existing items before adding new ones
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                    Log.i(TAG, type + " events fetched: " + list.size());
                } else {
                    Log.i(TAG, "Failed to load " + type + " events");
                    Toast.makeText(getContext(), "Failed to load " + type, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                Log.i(TAG, "Error fetching " + type + " events: " + t.getMessage());
                Toast.makeText(getContext(), "Error fetching " + type + ": " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showCreateEventDialog() {
        // Inflate the layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.create_event_dialog, null);

        // Find views by ID
        TextInputEditText etTitle = view.findViewById(R.id.etEventTitle);
        TextInputEditText etDescription = view.findViewById(R.id.etEventDescription);
        TextInputEditText etStartDate = view.findViewById(R.id.etEventStartDate);
        TextInputEditText etEndDate = view.findViewById(R.id.etEventEndDate);
        AutoCompleteTextView spinnerType = view.findViewById(R.id.spinnerEventType);

        // Initialize dropdown for event types using an ArrayAdapter
        String[] eventTypes = {"Schedule", "Practice", "Event"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, eventTypes);
        spinnerType.setAdapter(adapter);

        // Date Picker for Start Date
        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate));

        // Date Picker for End Date
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate));

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Create Event")
                .setView(view)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    // Handle saving the event
                    String title = etTitle.getText().toString();
                    String description = etDescription.getText().toString();
                    String startDate = etStartDate.getText().toString();
                    String endDate = etEndDate.getText().toString();
                    String type = spinnerType.getText().toString();

                    saveEvent(title, description, startDate, endDate, type);
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void showDateTimePicker(final TextInputEditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(Calendar.YEAR, selectedYear);
            calendar.set(Calendar.MONTH, selectedMonth);
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (timeView, selectedHour, selectedMinute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String selectedDateTime = iso8601Format.format(calendar.getTime());
                editText.setText(selectedDateTime);

            }, hour, minute, true); // True for 24-hour format

            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }


    private void saveEvent(String title, String description, String startDate, String endDate, String type) {
        try {
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));  // Set timezone to UTC
            Date parsedStartDate = iso8601Format.parse(startDate);
            Date parsedEndDate = iso8601Format.parse(endDate);

            createEvent(new EventModel(title, description, parsedStartDate, parsedEndDate, EventType.valueOf(type)));
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing dates in ISO 8601 format: " + e.getMessage());
        }
    }



    private void createEvent(EventModel eventModel) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        Log.d(TAG, "Creating event with JSON: " + gson.toJson(eventModel));

        ApiService apiService =RetrofitClient.getApiService(getContext());
        apiService.addEvent(eventModel).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(getView(), "Event created successfully", Snackbar.LENGTH_LONG).show();
                    fetchEventsForPreview("Event", eventsList, eventsAdapter);
                    fetchEventsForPreview("Practice", practicesList, practicesAdapter);
                    fetchEventsForPreview("Schedule", schedulesList, schedulesAdapter);
                    sendNotification("Event","Event Created Successfully!");
                } else {
                    Snackbar.make(getView(), "Failed to create event", Snackbar.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to create event: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Snackbar.make(getView(), "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "Network error while creating event: " + t.getMessage());
            }
        });
    }


    private void sendNotification(String eventType, String content) {
        Random random = new Random();
        int notificationId = random.nextInt(1000); // Generate a random number between 0 and 999
        String channelId = "2";
        notificationHelper.sendNotification(notificationId, channelId, eventType, content);
    }


}

class EventPreviewAdapter extends RecyclerView.Adapter<EventPreviewAdapter.EventViewHolder> {

    private List<EventModel> eventList;
    private LayoutInflater inflater;

    public EventPreviewAdapter(Context context, List<EventModel> eventList) {
        this.eventList = eventList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.event_preview, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
//        Log.i("DashboardFragment","event list: "+eventList.size());
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView eventDescriptionTextView;
        private TextView eventStartDateTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvEventTitle);
            eventDescriptionTextView = itemView.findViewById(R.id.tvEventDescription);
            eventStartDateTextView = itemView.findViewById(R.id.tvEventDate);
        }

        public void bind(EventModel event) {
            titleTextView.setText(event.getTitle());
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(itemView.getContext());
            String date = "";
            if (event.getEventStartDate() != null && event.getEventEndDate() != null) {
                date = dateFormat.format(event.getEventStartDate()) + " - " + dateFormat.format(event.getEventEndDate());
            } else if (event.getEventStartDate() != null) {
                date = dateFormat.format(event.getEventStartDate());
            } else if (event.getEventEndDate() != null) {
                date = dateFormat.format(event.getEventEndDate());
            }
            eventDescriptionTextView.setText(event.getDescription());
            eventStartDateTextView.setText(date);

            Log.d("EventPreviewAdapter", "Binding view for event: " + event.getTitle());
        }
    }
}