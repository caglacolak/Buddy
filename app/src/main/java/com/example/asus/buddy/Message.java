package com.example.asus.buddy;


import android.support.annotation.NonNull;

public class Message  implements Comparable<Message>{
    public long DeliveryTime;
    public String FromId;
    public String GroupId;
    public String Text;

    public Message() {
    }

    public Message(long DeliveryTime, String fromId, String groupId, String text) {
        this.DeliveryTime = DeliveryTime;
        FromId = fromId;
        GroupId = groupId;
        Text = text;
    }

    public long getDeliveryTime() {
        return DeliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        DeliveryTime = deliveryTime;
    }

    public String getFromId() {
        return FromId;
    }

    public void setFromId(String fromId) {
        FromId = fromId;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }



    @Override
    public int compareTo(@NonNull Message o) {
        int compare=getFromId().compareTo(o.getFromId());
        if (compare==0){
            compare=Long.compare(o.DeliveryTime,DeliveryTime);

        }
        /* For Ascending order*/
        return compare;
    }

    @Override
    public String toString() {
        return "Message{" +
                "DeliveryTime=" + DeliveryTime +
                ", FromId='" + FromId + '\'' +
                ", GroupId='" + GroupId + '\'' +
                ", Text='" + Text + '\'' +
                '}';
    }
}