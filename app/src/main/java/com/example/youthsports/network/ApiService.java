package com.example.youthsports.network;

import com.example.youthsports.model.AchievementModel;
import com.example.youthsports.model.ChatGroupModel;
import com.example.youthsports.model.EventModel;
import com.example.youthsports.model.MessageModel;
import com.example.youthsports.model.UserLogin;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/signin")
    Call<ApiResponse> signIn(@Body UserLogin user);

    @POST("auth/signup")
    Call<ApiResponse> signUp(@Body UserLogin newUser);

    @POST("auth/reset-password")
    Call<ApiResponse> resetPassword(@Query("userEmail") String userEmail, @Query("otp") String otp, @Query("newPassword") String newPassword);

    @POST("auth/request-reset-password")
    Call<ApiResponse> requestPasswordReset(@Query("userEmail") String userEmail);

    @GET("api/userinfo/getuserdetails")
    Call<UserLogin> getUserInformation();

    @PUT("api/userinfo/updateuserdetails")
    Call<ApiResponse> updateUserDetails(@Body UserLogin updatedUser);

    @GET("api/events/geteventsforpreview")
    Call<List<EventModel>> getEvents(@Query("type") String type,@Query("limit") int limit,@Query("page")int page);

    @POST("api/events/addevent")
    Call<ApiResponse> addEvent(@Body EventModel event);



    /*Chatting api's*/

    @POST("api/chat/creategroup")
    Call<ApiResponse> createGroup(@Query("groupname") String groupName,@Query("groupdescription") String groupDescription,@Query("creatorid") Long creatorId);

    @POST("api/chat/sendmessage")
    Call<MessageModel> sendMessage(@Query("groupid")Long groupId,@Query("senderid") Long senderId,@Query("message")String message);
    @GET("api/chat/getmessages")
    Call<List<MessageModel>> getMessagesByGroupId(@Query("groupid") Long groupId);

    @POST("api/chat/joingroup")
    Call<ApiResponse> joinGroup(@Query("groupid")Long groupId,@Query("userid") Long userId);


    @GET("api/chat/getjoinedchatgroups")
    Call<Set<ChatGroupModel>> getJoinedChatGroupsList (@Query("userid") long userid);

    @GET("api/chat/getallnotjoinedgroupchats")
    Call<List<ChatGroupModel>> getAllOtherChatGroupsListWhereUserNotJoined(@Query("userid") long userid);
    @PUT("api/chat/leavegroup")
    Call<ApiResponse> leaveGroupChat(@Query("groupid") Long groupid,@Query("userid") Long userid);


    /*Achievement Api's */

    @GET("api/achievements/getallachievements")
    Call<List<AchievementModel>> getAllAchievements();

    @POST("api/achievements/createachievement")
    Call<AchievementModel> createAnAchievement(@Body AchievementModel achievementModel);


}
