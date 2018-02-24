package com.example.asus.buddy;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ASUS on 13.12.2017.
 */

public class SendFragment extends android.support.v4.app.Fragment{
    @Nullable
    RecyclerView sendre;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private List<String> mDatakey=new ArrayList<>();
    private List<String> groupids=new ArrayList<>();


    formadaptersend cc;
    private List<Sendform> mygroups=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_send, container, false);

        sendre=(RecyclerView)view.findViewById(R.id.sendrecycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);


        mygroups.clear();
        sendre.setLayoutManager(layoutManager);

        loadData();
        //Log.i("TEST - MY GROUPS:",mygroups.toString());


        cc=new formadaptersend( mygroups,groupids, getActivity());
        sendre.setHasFixedSize(true);
        sendre.setAdapter(cc);



        sendre.setItemAnimator(new DefaultItemAnimator());
        sendre.getAdapter().notifyDataSetChanged();


        return view;
    }

    private void loadData() {


        auth= FirebaseAuth.getInstance();

        String userid = auth.getCurrentUser().getUid();
        String usermail=auth.getCurrentUser().getEmail();


        DatabaseReference sendusrgroup = FirebaseDatabase.getInstance().getReference().child("SentGroupInvitations").child(userid);

        sendusrgroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot single:dataSnapshot.getChildren()){
                    String temp=single.getKey().toString();

                    groupids.add(temp);
                    Log.e("First", single.getKey().toString());
                    getgroupinfo(temp);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void getgroupinfo(String temp) {
        String userid = auth.getCurrentUser().getUid();

        DatabaseReference groupinfo = FirebaseDatabase.getInstance().getReference().child("SentGroupInvitations").child(userid).child(temp);

        groupinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("First", dataSnapshot1.child("CreatedTime").getValue().toString());


                if (dataSnapshot.getValue()!=null){
                    String displaystatu=dataSnapshot.child("InvitationStatus").getValue(String.class);
                    Boolean displayisseen=dataSnapshot.child("IsSeen").getValue(Boolean.class);
                    String displayreceivedmail=dataSnapshot.child("ReceivedEmail").getValue(String.class);
                    Log.e("First", displayreceivedmail);

                    String displaysendingtime=dataSnapshot.child("SendingTime").getValue(String.class);

                    mygroups.add(new Sendform(displaystatu,displayisseen,displayreceivedmail,displaysendingtime));
                }



                cc.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    public static byte[] decrypt(String ivStr, String keyStr, byte[] bytes) throws Exception{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(ivStr.getBytes());
        byte[] ivBytes = md.digest();

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(keyStr.getBytes());
        byte[] keyBytes = sha.digest();

        return decrypt(ivBytes, keyBytes, bytes);
    }

    static byte[] decrypt(byte[] ivBytes, byte[] keyBytes, byte[] bytes)  throws Exception{
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(bytes);
    }
    public static String decryptStrAndFromBase64(String ivStr, String keyStr, String deStr) throws Exception{
        byte[] bytes = decrypt(keyStr, keyStr, Base64.decode(deStr.getBytes("UTF-8"),Base64.DEFAULT));
        return new String(bytes, "UTF-8");
    }
    public String hexToString(String txtInHex)
    {
        byte [] txtInByte = new byte [txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2)
        {
            txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }

}
