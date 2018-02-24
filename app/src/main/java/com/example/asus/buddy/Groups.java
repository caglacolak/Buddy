package com.example.asus.buddy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by CAGLA on 7.12.2017.
 */

public class Groups {

    private String CreatedTime;
    private String groupid;
    private String imageURL;
    private String LeaderId;
    private String groupname;
    private boolean isSeenLocation;
    private boolean seeOnMap;
    public String id;
    public ListFriend listFriend;

    public Groups(){
        listFriend = new ListFriend();
    }

    public Groups(String createdTime, String groupid, String imageURL, String leaderId, String groupname) {
        CreatedTime = createdTime;
        this.groupid = groupid;
        this.imageURL = imageURL;
        LeaderId = leaderId;
        this.groupname = groupname;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "CreatedTime='" + CreatedTime + '\'' +
                ", groupid='" + groupid + '\'' +
                ", imageURL=" + imageURL +
                ", LeaderId='" + LeaderId + '\'' +
                ", groupname='" + groupname + '\'' +
                ", isSeenLocation=" + isSeenLocation +
                ", seeOnMap=" + seeOnMap +
                '}';
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public void setLeaderId(String leaderId) {
        LeaderId = leaderId;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getLeaderId() {
        return LeaderId;
    }

    public String getGroupname() {
        return groupname;
    }

    public boolean isSeenLocation() {
        return isSeenLocation;
    }

    public boolean isSeeOnMap() {
        return seeOnMap;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public void setSeenLocation(boolean seenLocation) {
        isSeenLocation = seenLocation;
    }

    public void setSeeOnMap(boolean seeOnMap) {
        this.seeOnMap = seeOnMap;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


}
