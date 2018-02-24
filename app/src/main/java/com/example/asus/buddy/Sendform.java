package com.example.asus.buddy;

import java.util.Date;

/**
 * Created by ASUS on 14.12.2017.
 */

public class Sendform {
    String InvitationStatus;
    Boolean IsSeen;
    String ReceivedEmail;
    String SendingTime;

    public Sendform() {
    }

    public Sendform(String invitationStatus, Boolean isSeen, String receivedEmail, String sendingTime) {
        InvitationStatus = invitationStatus;
        IsSeen = isSeen;
        ReceivedEmail = receivedEmail;
        SendingTime = sendingTime;
    }

    public String getInvitationStatus() {
        return InvitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        InvitationStatus = invitationStatus;
    }

    public Boolean getSeen() {
        return IsSeen;
    }

    public void setSeen(Boolean seen) {
        IsSeen = seen;
    }

    public String getReceivedEmail() {
        return ReceivedEmail;
    }

    public void setReceivedEmail(String receivedEmail) {
        ReceivedEmail = receivedEmail;
    }

    public String getSendingTime() {
        return SendingTime;
    }

    public void setSendingTime(String sendingTime) {
        SendingTime = sendingTime;
    }
}
