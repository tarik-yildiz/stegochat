package com.yildiztarik.stegochat.Models;

public class MessageModel {
    String userId,otherId,messageType,date,message,from;

    public MessageModel() {
    }

    public MessageModel(String userId, String otherId, String messageType, String date, String message, String from) {
        this.userId = userId;
        this.otherId = otherId;
        this.messageType = messageType;
        this.date = date;
        this.message = message;
        this.from = from;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "userId='" + userId + '\'' +
                ", otherId='" + otherId + '\'' +
                ", messageType='" + messageType + '\'' +
                ", date='" + date + '\'' +
                ", message='" + message + '\'' +
                ", from='" + from + '\'' +
                '}';
    }
}
