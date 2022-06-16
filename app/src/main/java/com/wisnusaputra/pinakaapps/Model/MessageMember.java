package com.wisnusaputra.pinakaapps.Model;

public class MessageMember {

    String  message, time, date, type, senderuid, receiveruid;

    public MessageMember() {
    }

    public MessageMember(String message, String time, String date, String type, String senderuid, String receiveruid) {
        this.message = message;
        this.time = time;
        this.date = date;
        this.type = type;
        this.senderuid = senderuid;
        this.receiveruid = receiveruid;
    }

    @Override
    public String toString() {
        return "MessageMember{" +
                "message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", senderuid='" + senderuid + '\'' +
                ", receiveruid='" + receiveruid + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderuid() {
        return senderuid;
    }

    public void setSenderuid(String senderuid) {
        this.senderuid = senderuid;
    }

    public String getReceiveruid() {
        return receiveruid;
    }

    public void setReceiveruid(String receiveruid) {
        this.receiveruid = receiveruid;
    }
}
