package com.example.youthsports.model;


import java.util.HashSet;
import java.util.Set;


public class ChatGroupModel {

    private Long groupId;

    private String groupName;

	private String groupDescription;

	private Set<UserLogin> groupMembers = new HashSet<>();

    private Set<MessageModel> messages = new HashSet<>();

    public void addMessage(MessageModel message) {
        messages.add(message);
        message.setChatGroup(this);
    }

    public void addGroupMember(UserLogin user) {
        groupMembers.add(user);
        user.getChatGroups().add(this);
    }

    public void removeGroupMember(UserLogin user) {
        groupMembers.remove(user);
        user.getChatGroups().remove(this);
    }

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<UserLogin> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<UserLogin> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Set<MessageModel> getMessages() {
		return messages;
	}

	public void setMessages(Set<MessageModel> messages) {
		this.messages = messages;
	}


	@Override
	public String toString() {
		return "ChatGroupModel{" +
				"groupId=" + groupId +
				", groupName='" + groupName + '\'' +
				", groupDescription='" + groupDescription + '\'' +
				", groupMembers=" + groupMembers +
				", messages=" + messages +
				'}';
	}
}