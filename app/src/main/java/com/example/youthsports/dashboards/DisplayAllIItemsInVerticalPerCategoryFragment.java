package com.example.youthsports.dashboards;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.youthsports.R;
import com.example.youthsports.model.EventModel;
import com.example.youthsports.model.EventType;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.network.ApiService;
import com.example.youthsports.network.JwtTokenService;
import com.example.youthsports.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DisplayAllIItemsInVerticalPerCategoryFragment extends Fragment {

    private String TAG ="DisplayAllItemsInVerticalPerCategoryFragment";

    private RecyclerView rvEventsPreview;
    private EventPreviewAdapter eventsAdapter;


    CardView eventPreviewCardView;

    Toolbar toolbar;
    private List<EventModel> eventsList = new ArrayList<>();

    private static String category;

    public static DisplayAllIItemsInVerticalPerCategoryFragment newInstance(String categoryy) {
        category =categoryy;
        return new DisplayAllIItemsInVerticalPerCategoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_all_i_items_in_vertical_per_category, container, false);

        findViewByIds(view);

        rvEventsPreview.setLayoutManager(new LinearLayoutManager(getContext())); // Default orientation is vertical
        eventsAdapter = new EventPreviewAdapter(getContext(), eventsList);
        rvEventsPreview.setAdapter(eventsAdapter);

        fetchEventsForPreview(category, eventsList, eventsAdapter);

        return view;
    }
    private void findViewByIds(View view){
        rvEventsPreview = view.findViewById(R.id.rvEventsPreview);
        toolbar =view.findViewById(R.id.displayAllItemsInVerticalToolbar);

        toolbar.setNavigationOnClickListener(v->moveBack());

    }



    private void moveBack(){
        // Handle back button click
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }


    private void fetchEventsForPreview(String type, List<EventModel> list, EventPreviewAdapter adapter) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Log.i(TAG, "Fetching " + type + " events for preview");
        int page = 0;
        int limit = 10;

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




}