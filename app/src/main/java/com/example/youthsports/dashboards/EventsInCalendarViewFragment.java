package com.example.youthsports.dashboards;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.youthsports.R;
import com.example.youthsports.model.EventModel;
import com.example.youthsports.network.ApiService;
import com.example.youthsports.network.RetrofitClient;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsInCalendarViewFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private List<Event> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_in_calendar_view_fragment, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        loadEvents();
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            Event event = findEventByDate(date);
            if (event != null) {
                showDialogWithEventDetails(event);
            }
        });
        return view;
    }

    private void loadEvents() {
        fetchEventsForPreview("Event", this::updateCalendarEvents);
        fetchEventsForPreview("Practice", this::updateCalendarEvents);
        fetchEventsForPreview("Schedule", this::updateCalendarEvents);
    }

    private void updateCalendarEvents(List<EventModel> models) {
        for (EventModel model : models) {
            Event event = new Event(model.getTitle(), model.getEventStartDate());
            events.add(event);
            CalendarDay day = CalendarDay.from(event.date);
            calendarView.addDecorator(new EventDecorator(event.title, day, getContext()));
        }
    }

    private Event findEventByDate(CalendarDay date) {
        for (Event event : events) {
            CalendarDay eventDay = CalendarDay.from(event.date);
            if (eventDay.equals(date)) {
                return event;
            }
        }
        return null;
    }

    private void showDialogWithEventDetails(Event event) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(event.title)
                .setMessage("Event on " + event.date.toString())
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }

    private void fetchEventsForPreview(String type, Consumer<List<EventModel>> callback) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        apiService.getEvents(type, 3, 0).enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.accept(response.body());
                } else {
                    Toast.makeText(getContext(), "Failed to load " + type, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching " + type + ": " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class EventDecorator implements DayViewDecorator {
        private final String title;
        private final CalendarDay date;
        private final Context context;

        public EventDecorator(String title, CalendarDay date, Context context) {
            this.title = title;
            this.date = date;
            this.context = context;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new ForegroundColorSpan(Color.RED));
            view.addSpan(new RelativeSizeSpan(1.2f));
        }
    }

    static class Event {
        String title;
        Date date;

        Event(String title, Date date) {
            this.title = title;
            this.date = date;
        }
    }
}
