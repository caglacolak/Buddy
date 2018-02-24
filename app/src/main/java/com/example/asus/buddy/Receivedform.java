package com.example.asus.buddy;

import java.util.Date;

/**
 * Created by ASUS on 14.12.2017.
 */

public class Receivedform {

    String InvitationStatus;
    Boolean IsSeen;
    String  LeaderId;
    String SendingTime;
    String Title;

    public Receivedform() {
    }

    public Receivedform(String invitationStatus, Boolean isSeen, String leaderId, String sendingTime, String title) {
        InvitationStatus = invitationStatus;
        IsSeen = isSeen;
        LeaderId = leaderId;
        SendingTime = sendingTime;
        Title = title;
    }

    public void setInvitationStatus(String invitationStatus) {
        InvitationStatus = invitationStatus;
    }

    public void setSeen(Boolean seen) {
        IsSeen = seen;
    }

    public void setLeaderId(String leaderId) {
        LeaderId = leaderId;
    }

    public void setSendingTime(String sendingTime) {
        SendingTime = sendingTime;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getInvitationStatus() {
        return InvitationStatus;
    }

    public Boolean getSeen() {
        return IsSeen;
    }

    public String getLeaderId() {
        return LeaderId;
    }

    public String getSendingTime() {
        return SendingTime;
    }

    public String getTitle() {
        return Title;
    }
}
