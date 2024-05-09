package com.example.youthsports.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class EventModel {

    private Long id;

    private String title;

    private String description;
    private Date eventStartDate;

    private Date eventEndDate;
    private EventType type; // Could be "schedule", "practice", or "event"

    private UserLogin createdUser; // Assuming UserLogin is your user entity




    public EventModel() {
        super();
    }

    public EventModel(Long id, String title, String description, Date eventStartDate, Date eventEndDate, EventType type) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.type = type;
    }

    public EventModel(String title, String description, Date eventStartDate, Date eventEndDate, EventType type) {
        this.title = title;
        this.description = description;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public UserLogin getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(UserLogin createdUser) {
        this.createdUser = createdUser;
    }


    @Override
    public String toString() {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String formattedStartDate = eventStartDate != null ? iso8601Format.format(eventStartDate) : "null";
        String formattedEndDate = eventEndDate != null ? iso8601Format.format(eventEndDate) : "null";

        return "EventModel{id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", eventStartDate=" + formattedStartDate +
                ", eventEndDate=" + formattedEndDate +
                ", type=" + type +"}";
    }

}
