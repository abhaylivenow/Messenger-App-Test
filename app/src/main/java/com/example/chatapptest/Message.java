package com.example.chatapptest;

public class Message {
    private String message;
    private String messageId;
    private String senderId;
    private long timeStamp;
    private int feelings;

    public Message(){
        
    }

    public Message(String message, String senderId, long timeStamp) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFeelings() {
        return feelings;
    }

    public void setFeelings(int feelings) {
        this.feelings = feelings;
    }
}
