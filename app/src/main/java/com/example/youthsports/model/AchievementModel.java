package com.example.youthsports.model;

import java.util.Date;

public class AchievementModel {

    private Long id;


    private UserLogin achievedUser;


//    private UserLogin awardedUser;


    private String title;

    private String description;

    private Date awardedOn;

    public AchievementModel(Long id) {
        this.id = id;
    }
    public AchievementModel(){

    }

    public AchievementModel(UserLogin achievedUser, String title, String description, Date awardedOn) {
        this.achievedUser = achievedUser;
        this.title = title;
        this.description = description;
        this.awardedOn = awardedOn;
    }

    public AchievementModel(String title,String description,Date awardedOn,UserLogin achievedUser){
        this.title = title;
        this.description = description;
        this.awardedOn = awardedOn;
        this.achievedUser = achievedUser;
    }


    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserLogin getAchievedUser() {
        return achievedUser;
    }

    public void setAchievedUser(UserLogin achievedUser) {
        this.achievedUser = achievedUser;
    }

//    public UserLogin getAwardedUser() {
//        return awardedUser;
//    }
//
//    public void setAwardedUser(UserLogin awardedUser) {
//        this.awardedUser = awardedUser;
//    }

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

    public Date getAwardedOn() {
        return awardedOn;
    }

    public void setAwardedOn(Date awardedOn) {
        this.awardedOn = awardedOn;
    }

    @Override
    public String toString() {
        return "AchievementModel{" +
                "id=" + id +
                ", achievedUser=" + achievedUser +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", awardedOn=" + awardedOn +
                '}';
    }
}
