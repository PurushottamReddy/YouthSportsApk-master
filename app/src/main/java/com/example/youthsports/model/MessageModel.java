package com.example.youthsports.model;



import java.util.Date;


public class MessageModel {
	

    private Long id;
    private ChatGroupModel chatGroup;
    private UserLogin sender;

	private String message;
	private Date timestamp = new Date();

	private Long senderId;
	private String senderName;

    public void setChatGroup(ChatGroupModel chatGroup) {
        this.chatGroup = chatGroup;
        chatGroup.getMessages().add(this);
    }

    public void setSender(UserLogin sender) {
        this.sender = sender;
        sender.getMessages().add(this);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public ChatGroupModel getChatGroup() {
		return chatGroup;
	}

	public UserLogin getSender() {
		return sender;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	@Override
	public String toString() {
		return "MessageModel [id=" + id + ", chatGroup=" + chatGroup + ", sender=" + sender + ", message=" + message
				+ ", timestamp=" + timestamp + "]";
	}

	
    
    
}
