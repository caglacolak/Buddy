package com.example.asus.buddy;

/**
 * Created by ASUS on 22.12.2017.
 */

public class SendInvitation {

    String InvitationStatus;
    Boolean IsSeen;
    String ReceivedEmail;
    String SendingTime;

    public SendInvitation(String invitationStatus, Boolean isSeen, String receivedEmail, String sendingTime) {
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

    public Boolean getIsSeen() {
        return IsSeen;
    }

    public void setIsSeen(Boolean isSeen) {
        IsSeen = isSeen;
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
