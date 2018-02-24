package com.example.asus.buddy;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.Collections;
import java.util.List;

/**
 * Created by ASUS on 30.12.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private DatabaseReference mDatabase;

    private List<Message> messageList;
    private List<Message> messageList2;

    private FirebaseAuth mAuth;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_message_friend,parent,false);
        return new MessageViewHolder(v);

    }
    String fromname,image;
    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        String current_user=mAuth.getCurrentUser().getUid();
        Message c=messageList.get(position);
        String from_user=c.getFromId();

        DatabaseReference usrmessagesid = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        usrmessagesid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fromname=dataSnapshot.child("Name").getValue().toString();
                if (dataSnapshot.child("ImageUrlPath").getValue()!=null&&dataSnapshot.child("ImageUrlPath").getValue()!=" "){
                    image=dataSnapshot.child("ImageUrlPath").getValue().toString();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (image==null||image.equals(" ")){

        }else {
            Picasso.with(holder.avata.getContext()).load(image)
                    .placeholder(R.drawable.default_avata).into(holder.avata);
        }

        if (from_user.equals(current_user)){
            holder.name.setText(fromname);


        }else {
            holder.txtContent.setBackgroundColor(Color.WHITE);
            holder.txtContent.setTextColor(Color.BLACK);
            holder.name.setText(fromname);

        }

        holder.txtContent.setText(c.getText());





    }

    @Override
    public int getItemCount() {
            return messageList.size();


    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView txtContent,name;
        public ImageView avata;

        public MessageViewHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
            avata = (ImageView) itemView.findViewById(R.id.imageView3);
            name=(TextView)itemView.findViewById(R.id.messagefromname);
        }

    }
}
