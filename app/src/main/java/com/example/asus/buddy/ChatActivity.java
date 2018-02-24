package com.example.asus.buddy;

/**
 * Created by ASUS on 10.12.2017.
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatActivity  extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerChat;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    private MessageAdapter adapter;
    private String roomId,roomname;
    private Consersation consersation;
    private ImageButton btnSend;
    private EditText editWriteMessage;
    private LinearLayoutManager linearLayoutManager;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public Bitmap bitmapAvataUser;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private final List<Message> mMessageList=new ArrayList<>();
    private List<Message> lastmessage=new ArrayList<>();


    protected void onStart(){
        super.onStart();
        loadmessages();

        Collections.sort(mMessageList);
        Log.e("mesaajlar",mMessageList.toString());


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intentData = getIntent();
        roomId = intentData.getStringExtra("groupid");
        roomname = intentData.getStringExtra("groupname");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        String userid = auth.getCurrentUser().getUid();
        consersation = new Consersation();
        btnSend = (ImageButton)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);
        if (roomname != null ) {

            getSupportActionBar().setTitle(roomname);

        }
        lastmessage=sort(mMessageList);

        adapter = new MessageAdapter(lastmessage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recyclerChat.setHasFixedSize(true);
        recyclerChat.setLayoutManager(layoutManager);


        recyclerChat.setAdapter(adapter);

        mMessageList.clear();


        }


    public void loadmessages(){

        FirebaseDatabase.getInstance().getReference().child("Messages/" + roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessageList.clear();

                for (DataSnapshot single:dataSnapshot.getChildren()) {

                    String temp =single.getKey().toString();
                    Log.e("snapshot", temp);


                    getmessages(temp);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }
    /*public class BubbleSortExample {
        static void bubbleSort(int[] arr) {
            int n = arr.length;
            int temp = 0;
            for(int i=0; i < n; i++){
                for(int j=1; j < (n-i); j++){
                    if(arr[j-1] > arr[j]){
                        //swap elements
                        temp = arr[j-1];
                        arr[j-1] = arr[j];
                        arr[j] = temp;
                    }

                }
            }

        }*/
        List<Message> sort(List<Message> arr)
    {
        Log.e("arraysize", String.valueOf(arr.size()));

        int n = arr.size();
            for(int x=0;x<n-1;x++){
                for(int y=0;y<(n-x-1);y++){
                    if(arr.get(y).getDeliveryTime()>arr.get(y+1).getDeliveryTime()){
                        Message m = arr.get(y);
                        arr.get(y).setDeliveryTime(arr.get(y+1).getDeliveryTime());
                        arr.get(y).setGroupId(arr.get(y+1).getGroupId());
                        arr.get(y).setFromId(arr.get(y+1).getFromId());
                        arr.get(y).setText(arr.get(y+1).getText());
                        Log.e("deliverytime", String.valueOf(arr.get(y+1).getDeliveryTime()));
                        arr.get(y+1).setDeliveryTime(m.getDeliveryTime());
                        arr.get(y+1).setGroupId(m.getGroupId());
                        arr.get(y+1).setFromId(m.getFromId());
                        arr.get(y+1).setText(m.getText());


                    }
                }
            }
            return arr;
    }

    public void getmessages(String useerid){


        final String userid = useerid;

        DatabaseReference usrmessagesid = FirebaseDatabase.getInstance().getReference().child("Messages").child(roomId).child(useerid);
        usrmessagesid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mMessageList.clear();

                for (DataSnapshot single:dataSnapshot.getChildren()) {
                    String temp = single.getKey().toString();
                    Log.e("usermessageid",temp);
                    usermessagess(userid,temp);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void usermessagess(String userrid,String messageid){
        DatabaseReference usrmessages = FirebaseDatabase.getInstance().getReference().child("Messages").child(roomId).child(userrid).child(messageid);

        usrmessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    Log.e("mesaagess", dataSnapshot.getValue().toString());

                    Message newMessage = new Message();
                    newMessage.DeliveryTime = Long.parseLong(dataSnapshot.child("DeliveryTime").getValue().toString());
                    newMessage.FromId = dataSnapshot.child("FromId").getValue().toString();
                    newMessage.GroupId = dataSnapshot.child("GroupId").getValue().toString();
                    newMessage.Text = dataSnapshot.child("Text").getValue().toString();
                    mMessageList.add(newMessage);

                    recyclerChat.scrollToPosition(mMessageList.size() - 1);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onClick(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        String userid = auth.getCurrentUser().getUid();

        if (view.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                editWriteMessage.setText("");
                HashMap newMessage = new HashMap();
                newMessage.put("DeliveryTime",System.currentTimeMillis());
                newMessage.put("FromId" ,userid) ;
                newMessage.put("GroupId", roomId);
                newMessage.put("Text",content);

                FirebaseDatabase.getInstance().getReference().child("Messages/" + roomId).child(userid).push().setValue(newMessage);

                mMessageList.clear();

            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.navmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.editgroup:
                Intent editg = new Intent();
                editg.setClass(ChatActivity.this, EditGroupActivity.class);
                editg.putExtra("groupid",roomId);
                startActivity(editg);
                break;
            case R.id.addfriends:
                Intent addfr = new Intent();
                addfr.setClass(ChatActivity.this, AddFriend.class);
                addfr.putExtra("groupid",roomId);
                addfr.putExtra("groupname",roomname);

                startActivity(addfr);
                break;
            case R.id.createeventt:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.putExtra("groupid",roomId);

                startActivity(intent);
        }
        return true;
    }
}



