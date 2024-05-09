package com.example.youthsports.dashboards;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youthsports.R;
import com.example.youthsports.model.MessageModel;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.network.ApiService;
import com.example.youthsports.network.JwtTokenService;
import com.example.youthsports.network.RetrofitClient;
import com.example.youthsports.util.UserDetailsInSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatFragment extends Fragment {

    private String TAG ="ChatFragment";
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<MessageModel> messagesOfGroupList=new ArrayList<>(); // This should be fetched from your server or local database

    private EditText sendMsgEdtTxt;
    private LinearLayout leaveGroupChatLayout;
    private Button sendMsgBtn;
    private Toolbar toolbar;
    private static Long groupId;
    private static String groupName;

    public static ChatFragment newInstance(Long groupid,String groupname) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong("groupId", groupid);
        groupId =groupid;
        groupName =groupname;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        findViewByIds(view);
        getMessagesByGroupId(groupId);

        chatAdapter = new ChatAdapter(messagesOfGroupList);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
    private void findViewByIds(View view){

        sendMsgEdtTxt = view.findViewById(R.id.etMessage);
        sendMsgBtn = view.findViewById(R.id.btnSend);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        toolbar = view.findViewById(R.id.toolbarInGroupChat);
        toolbar.setTitle("Group : "+groupName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  moveBackToListOfGroups();
            }
        });
        leaveGroupChatLayout = view.findViewById(R.id.leave_group_chat_layout);

        leaveGroupChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveGroupChat(groupId,Long.parseLong(UserDetailsInSharedPreferences.getValue("userId")));
            }
        });


    }

    private void moveBackToListOfGroups(){
        // Handle back button click
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    private void sendMessage() {
        String messageTxt = sendMsgEdtTxt.getText().toString();
        if (messageTxt!=null){
         sendMessageToBackend(messageTxt);
        }
    }

    private void sendMessageToBackend(String messageTxt){
        ApiService apiService = RetrofitClient.getApiService(getContext());
        apiService.sendMessage(groupId,Long.parseLong(UserDetailsInSharedPreferences.getValue("userId")),messageTxt).enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {

                if (response.isSuccessful() && response.body()!=null){
                    sendMsgEdtTxt.setText("");
                    getMessagesByGroupId(groupId);
                }else{
                    Log.i(TAG, "Failed to send message " + groupId + " messages");
                    Toast.makeText(getContext(), "Failed to send messages of " + groupId+" ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {
                Log.i(TAG, "Error sending message to " + groupId + " messages: " + t.getMessage());
                Toast.makeText(getContext(), "Error sending message to " + groupId + ": " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Example method to fetch messages, you need to implement according to your logic
    private void getMessagesByGroupId(Long groupId) {
        ApiService apiService = RetrofitClient.getApiService(getContext());

        apiService.getMessagesByGroupId(groupId).enqueue(new Callback<List<MessageModel>>() {
            @Override
            public void onResponse(Call<List<MessageModel>> call, Response<List<MessageModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messagesOfGroupList.clear(); // Clear the existing items before adding new ones
                    messagesOfGroupList.addAll(response.body());
                    chatAdapter.notifyDataSetChanged(); // Notify the adapter about the data change
                    messagesOfGroupList.forEach(i-> Log.i(TAG,"message : " +i));
                    Log.i(TAG,  " messages for groupId: "+groupId +" is fetched and size is: " + messagesOfGroupList.size());
                } else {
                    Log.i(TAG, "Failed to load " + groupId + " messages");
                    Toast.makeText(getContext(), "Failed to load messages of " + groupId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MessageModel>> call, Throwable t) {
                Log.i(TAG, "Error fetching " + groupId + " messages: " + t.getMessage());
                Toast.makeText(getContext(), "Error fetching " + groupId + ": " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void leaveGroupChat(Long groupId,Long userId){

        ApiService apiService = RetrofitClient.getApiService(getContext());
        apiService.leaveGroupChat(groupId,userId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if(response.body().isSuccess())
                {
                    Log.i(TAG,"user with userid: "+userId+ " Exited Group with group id"+ groupId +"Successfully!");
                    Toast.makeText(getContext(),"Exited Group Successfully!",Toast.LENGTH_SHORT).show();
                    moveBackToListOfGroups();
                }else{
                    Toast.makeText(getContext(),"Unable to exit group "+response.body().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                Toast.makeText(getContext(),"Unable to exit: "+t.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}


class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageModel> messages;
    private final int VIEW_TYPE_SENT = 1;
    private final int VIEW_TYPE_RECEIVED = 2;


    public ChatAdapter(List<MessageModel> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messages.get(position);
        if (message.getSenderId() == Long.parseLong(UserDetailsInSharedPreferences.getValue("userId"))) { // Assume Message class has isSent method
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        if (holder instanceof SentMessageHolder) {
            ((SentMessageHolder) holder).bind(message);
        } else {
            ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvMessage, tvTimestamp;

        SentMessageHolder(View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        void bind(MessageModel message) {
            tvMessage.setText(message.getMessage());
            tvTimestamp.setText(DateUtils.formatDateTime(message.getTimestamp()));
            tvSenderName.setText(UserDetailsInSharedPreferences.getValue("name"));
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvMessage, tvTimestamp;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        void bind(MessageModel message) {
            tvMessage.setText(message.getMessage());
            tvTimestamp.setText(DateUtils.formatDateTime(message.getTimestamp()));
            tvSenderName.setText(message.getSenderName());
        }
    }
}
class DateUtils {
    public static String formatDateTime(Date timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.getDefault());
        return formatter.format(timestamp);
    }
}
