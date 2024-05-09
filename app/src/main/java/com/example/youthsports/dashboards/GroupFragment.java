package com.example.youthsports.dashboards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youthsports.R;
import com.example.youthsports.model.ChatGroupModel;
import com.example.youthsports.network.ApiResponse;
import com.example.youthsports.network.ApiService;
import com.example.youthsports.network.RetrofitClient;
import com.example.youthsports.util.UserDetailsInSharedPreferences;
import com.example.youthsports.util.YouthSportsUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupFragment extends Fragment implements JoinedGroupsChatAdapter.ItemClickListener , AvailableGroupsAdapter.ItemClickListener{

    private String TAG = "GroupFragment";
    private RecyclerView joinedGroupsChatRecyclerView,availableGroupChatRecyclerView;

    private FloatingActionButton createGroupChatFab;

    private TextView errorTextview;

    private View parentView;
    private JoinedGroupsChatAdapter joinedGroupsChatAdapter;

    private AvailableGroupsAdapter availableGroupsAdapter;

    private List<ChatGroupModel> joinedGroupList = new ArrayList<>();

    private List<ChatGroupModel> availableGroupList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        parentView = view;
        findViewByIds(view);

        joinedGroupsChatRecyclerView = view.findViewById(R.id.joinedGroupsChatRecyclerView);
        joinedGroupsChatAdapter = new JoinedGroupsChatAdapter(joinedGroupList, getContext(), this);
        joinedGroupsChatRecyclerView.setAdapter(joinedGroupsChatAdapter);
        joinedGroupsChatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.i(TAG, "Joined Groups RecyclerView initialized and layout manager set");

        availableGroupChatRecyclerView = view.findViewById(R.id.availableGroupChatRecyclerView);
        availableGroupsAdapter = new AvailableGroupsAdapter(availableGroupList, getContext(), this);
        availableGroupChatRecyclerView.setAdapter(availableGroupsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        availableGroupChatRecyclerView.setLayoutManager(layoutManager);
        Log.i(TAG, "Available Groups RecyclerView initialized and layout manager set");

        // Log context used for setting layout manager
        if (getContext() != null) {
            Log.i(TAG, "Context used for setting layout manager: " + getContext().getClass().getSimpleName());
        } else {
            Log.e(TAG, "Context is null when setting layout manager");
        }

        getJoinedChatGroupsList();
        getAllOtherChatGroupsListWhereUserNotJoined();
        return view;
    }

    private void findViewByIds(View view){
        errorTextview  =view.findViewById(R.id.group_chat_error_msg);
        createGroupChatFab = view.findViewById(R.id.creategroupchatfab);
        Long userId = Long.parseLong(UserDetailsInSharedPreferences.getValue("userId"));
        String accountType = UserDetailsInSharedPreferences.getValue("accountType");
        if(accountType!=null && !accountType.isEmpty() && (accountType.contentEquals("Coach") || accountType.contentEquals("Player"))){
            createGroupChatFab.setVisibility(View.VISIBLE);
            createGroupChatFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an AlertDialog builder instance with the current context.
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    // Get the layout inflater
                    LayoutInflater inflater = LayoutInflater.from(view.getContext());

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    View dialogView = inflater.inflate(R.layout.create_group_chat_layout_in_dialog, null);
                    builder.setView(dialogView);

                    // Reference to the EditText fields in the dialog layout
                    EditText groupNameInput = dialogView.findViewById(R.id.groupNameInput);
                    EditText groupDescriptionInput = dialogView.findViewById(R.id.groupDescriptionInput);

                    // Set up the buttons
                    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // Get the input text from EditText
                            String groupName = groupNameInput.getText().toString();
                            String groupDescription = groupDescriptionInput.getText().toString();
                            createGroupChat(groupName,groupDescription);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            Log.i(TAG,"Fab is visible because user is of type: "+accountType);
        }else{
            Log.i(TAG,"Fab is not visible because user is of type: "+accountType);
        }
    }


    private void setErrorTextview(){
        if(availableGroupList.size()==0 && joinedGroupList.size() == 0)
            errorTextview.setVisibility(View.VISIBLE);
        else
            errorTextview.setVisibility(View.INVISIBLE);
    }
    private void getJoinedChatGroupsList() {
        Long userId = Long.parseLong(UserDetailsInSharedPreferences.getValue("userId"));
        Log.i(TAG, "Fetching joined groups for user ID: " + userId);
        ApiService apiService = RetrofitClient.getApiService(getContext());
        apiService.getJoinedChatGroupsList(userId).enqueue(new Callback<Set<ChatGroupModel>>() {
            @Override
            public void onResponse(Call<Set<ChatGroupModel>> call, Response<Set<ChatGroupModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    joinedGroupList.clear();
                    joinedGroupList.addAll(response.body());
                    joinedGroupsChatAdapter.notifyDataSetChanged();
                    Log.i(TAG, "User groups fetched: " + joinedGroupList.size());
                } else {
                    Log.e(TAG, "Failed to load groups of user " + userId);
                    Toast.makeText(getContext(), "Failed to load groups list for user " + userId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Set<ChatGroupModel>> call, Throwable t) {
                Log.e(TAG, "Error fetching joined groups: " + t.getMessage());
                Toast.makeText(getContext(), "Error fetching groups: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllOtherChatGroupsListWhereUserNotJoined() {
        Long userId = Long.parseLong(UserDetailsInSharedPreferences.getValue("userId"));
        Log.i(TAG, "Fetching all other chat groups where user not joined for user ID: " + userId);
        ApiService apiService = RetrofitClient.getApiService(getContext());

        apiService.getAllOtherChatGroupsListWhereUserNotJoined(userId).enqueue(new Callback<List<ChatGroupModel>>() {
            @Override
            public void onResponse(Call<List<ChatGroupModel>> call, Response<List<ChatGroupModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableGroupList.clear();
                    availableGroupList.addAll(response.body());
                    availableGroupsAdapter.notifyDataSetChanged();
                    availableGroupList.forEach(i -> Log.i(TAG, "Available group: " + i));
                    setErrorTextview();
                    Log.i(TAG, "User groups fetched: " + joinedGroupList.size());
                } else {
                    Log.e(TAG, "Failed to load groups where user not joined for user " + userId);
                    Toast.makeText(getContext(), "Failed to load groups list for user " + userId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChatGroupModel>> call, Throwable t) {
                Log.e(TAG, "Error fetching groups where user not joined: " + t.getMessage());
                Toast.makeText(getContext(), "Error fetching groups: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinGroupChat(long groupId) {
        Long userId = Long.parseLong(UserDetailsInSharedPreferences.getValue("userId"));
        Log.i(TAG, "Joining group chat for user ID: " + userId + " and group ID: " + groupId);
        ApiService apiService = RetrofitClient.getApiService(getContext());

        apiService.joinGroup(groupId, userId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    ChatGroupModel joinedGroup = availableGroupList.stream()
                            .filter(group -> group.getGroupId() == groupId)
                            .findFirst()
                            .orElse(null);

                    if (joinedGroup != null) {
                        availableGroupList.remove(joinedGroup);
                        joinedGroupList.add(joinedGroup);
                        joinedGroupsChatAdapter.notifyDataSetChanged();
                        Log.i(TAG, "User joined group: " + joinedGroup.getGroupName());
                        setErrorTextview();
                    } else {
                        Log.e(TAG, "Failed to find joined group");
                    }
                } else {
                    Log.e(TAG, "Failed to join group: " + response.body());
                    Toast.makeText(getContext(), "Failed to join group: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error joining group: " + t.getMessage());
                Toast.makeText(getContext(), "Error joining group: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createGroupChat(String groupName,String description){
        Long creatorId= Long.parseLong(UserDetailsInSharedPreferences.getValue("userId"));
        ApiService apiService = RetrofitClient.getApiService(getContext());

        apiService.createGroup(groupName,description,creatorId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                  if(response.isSuccessful() && response.body() != null){
                      getJoinedChatGroupsList();
                      Log.i(TAG,"Successfully created group"+response);
                      YouthSportsUtil.showSnackbar(parentView,"Successfully created group ! ");
                  }else{
                      YouthSportsUtil.showSnackbar(parentView,"Unable to create a group!");
                      Log.i(TAG,"Unable to create group"+response);
                  }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.i(TAG,"Unable to create Group");
                Log.i(TAG,"Due to "+t.getMessage());
            }
        });



    }

    @Override
    public void onItemClick(View view, int position,String type) {
        if (type.contentEquals("Joined")) {
            ChatGroupModel selectedGroup = joinedGroupList.get(position);
            moveToChatsFragment(selectedGroup.getGroupId(), selectedGroup.getGroupName());
        }else{
           joinGroupChat(availableGroupList.get(position).getGroupId());
        }
    }
    private void moveToChatsFragment(Long groupId,String groupName) {
        Fragment chatFragment = ChatFragment.newInstance(groupId,groupName);
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}

class JoinedGroupsChatAdapter extends RecyclerView.Adapter<JoinedGroupsChatAdapter.ViewHolder> {

    private String TAG = "GroupFragment";
    private List<ChatGroupModel> groups;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    JoinedGroupsChatAdapter(List<ChatGroupModel> groups, Context context, ItemClickListener clickListener) {
        this.groups = groups;
        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_chat_groups_list, parent, false);
        Log.i(TAG, "JoinedGroupsChatAdapter: onCreateViewHolder");
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatGroupModel group = groups.get(position);
        holder.tvGroupName.setText(group.getGroupName());
        holder.tvGroupDescription.setText(group.getGroupDescription()); // Placeholder
        Log.i(TAG, "JoinedGroupsChatAdapter: onBindViewHolder for position " + position);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgGroupIcon;
        TextView tvGroupName, tvGroupDescription;
        ItemClickListener listener;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            imgGroupIcon = itemView.findViewById(R.id.imgGroupIcon);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupDescription = itemView.findViewById(R.id.tvGroupDescription);
            this.listener = listener;
            itemView.setOnClickListener(this);
            Log.i(TAG, "JoinedGroupsChatAdapter: ViewHolder created");
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition(), "Joined");
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, String type);
    }
}
class AvailableGroupsAdapter extends RecyclerView.Adapter<AvailableGroupsAdapter.ViewHolder> {

    private List<ChatGroupModel> availableGroups;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    private String TAG = "GroupFragment";

    AvailableGroupsAdapter(List<ChatGroupModel> groups, Context context, ItemClickListener clickListener) {
        this.availableGroups = groups;
        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_join_chat_group_list, parent, false);
        Log.i(TAG, "AvailableGroupsAdapter: onCreateViewHolder");
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatGroupModel group = availableGroups.get(position);
        holder.tvGroupName.setText(group.getGroupName());
        holder.tvGroupDescription.setText(group.getGroupDescription()); // Placeholder
        Log.i(TAG, "AvailableGroupsAdapter: onBindViewHolder for position " + position);
    }

    @Override
    public int getItemCount() {
        return availableGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgGroupIcon;
        TextView tvGroupName, tvGroupDescription;
        Button joinButton;
        ItemClickListener listener;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            imgGroupIcon = itemView.findViewById(R.id.imgGroupIcon);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupDescription = itemView.findViewById(R.id.tvGroupDescription);
            joinButton = itemView.findViewById(R.id.joingroupChatbtn);

            this.listener = listener;
            joinButton.setOnClickListener(this);
            Log.i(TAG, "AvailableGroupsAdapter: ViewHolder created");
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition(), "Available");
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, String type);
    }

}