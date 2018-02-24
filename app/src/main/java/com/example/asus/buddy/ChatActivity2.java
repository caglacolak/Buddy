package com.example.asus.buddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by ASUS on 18.12.2017.
 */

public class ChatActivity2 extends AppCompatActivity implements View.OnClickListener  {
    String grupid;
    private RecyclerView recyclerChat;
    private LinearLayoutManager linearLayoutManager;

    private Consersation consersation;

    private EditText editWriteMessage;

    private ImageButton btnSend;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intentData = getIntent();
        grupid = intentData.getStringExtra("groupid");

        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                editWriteMessage.setText("");
                Message newMessage = new Message();
                newMessage.Text = content;
                newMessage.FromId = StaticConfig.UID;
                newMessage.GroupId = grupid;
                newMessage.DeliveryTime = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + grupid).push().setValue(newMessage);
            }
        }
    }
}
