package com.example.asus.buddy;



public class User {
    public String name;
    public String email;
    public String avata;
    public Status status;
    public Message message;


    public User(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timestamp = 0;
        message.GroupId = "0";
        message.FromId = "0";
        message.Text = "";
        message.DeliveryTime = 0;
    }
}
