package com.app.rohit.campk12_videochat.Models;

/**
 * Created by rohit on 18/1/18.
 */

public class Chat {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public String type;
    public long timestamp;

    public Chat() {}

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message,String type, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.type=type;
        this.timestamp = timestamp;
    }
}
