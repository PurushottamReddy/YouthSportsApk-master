package com.example.youthsports.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UserLogin {

    private Long userId;
    private String userEmail;
    private String password;
    private String name;

    private String contactNumber;
    private Boolean isUserVerified;
    private Date createdTimeStamp;
    private Date lastLoginTimeStamp;
    private AccountType accountType;
    private String emailVerificationToken;
    private Date emailVerificationTokenExpiry;


    private Set<ChatGroupModel> chatGroups = new HashSet<>();

    private Set<MessageModel> messages = new HashSet<>();

    // Constructor
    public UserLogin() {
    }
    public UserLogin(long userId){
        this.userId = userId;
    }

    public UserLogin(String userEmail,String userPassword){
        this.userEmail=userEmail;
        this.password=userPassword;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsUserVerified() {
        return isUserVerified;
    }

    public void setIsUserVerified(Boolean isUserVerified) {
        this.isUserVerified = isUserVerified;
    }

    public Date getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Date createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public Date getLastLoginTimeStamp() {
        return lastLoginTimeStamp;
    }

    public void setLastLoginTimeStamp(Date lastLoginTimeStamp) {
        this.lastLoginTimeStamp = lastLoginTimeStamp;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Date getEmailVerificationTokenExpiry() {
        return emailVerificationTokenExpiry;
    }

    public void setEmailVerificationTokenExpiry(Date emailVerificationTokenExpiry) {
        this.emailVerificationTokenExpiry = emailVerificationTokenExpiry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Boolean getUserVerified() {
        return isUserVerified;
    }

    public void setUserVerified(Boolean userVerified) {
        isUserVerified = userVerified;
    }

    public Set<ChatGroupModel> getChatGroups() {
        return chatGroups;
    }

    public void setChatGroups(Set<ChatGroupModel> chatGroups) {
        this.chatGroups = chatGroups;
    }

    public Set<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageModel> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", isUserVerified=" + isUserVerified +
                ", createdTimeStamp=" + createdTimeStamp +
                ", lastLoginTimeStamp=" + lastLoginTimeStamp +
                ", accountType=" + accountType +
                ", emailVerificationToken='" + emailVerificationToken + '\'' +
                ", emailVerificationTokenExpiry=" + emailVerificationTokenExpiry +
                ", chatGroups=" + chatGroups +
                ", messages=" + messages +
                '}';
    }
}
