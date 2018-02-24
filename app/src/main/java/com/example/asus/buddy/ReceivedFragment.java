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
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ASUS on 13.12.2017.
 */

public class ReceivedFragment extends android.support.v4.app.Fragment {
    RecyclerView re;
    formadapter cc;
    String keyStr,enmail,encMail;
    String ivStr;
    Button acceptb;
    private static final char[] HEX_CHARS = "gqLOHUioQ0QjhuvI".toCharArray();

    private List<Receivedform> mygroups=new ArrayList<>();
    private List<Groups> mygroupss=new ArrayList<>();
    private List<String> groupids=new ArrayList<>();



    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    String statu;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_received, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        re=(RecyclerView)view.findViewById(R.id.receivedrecycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);



        re.setLayoutManager(layoutManager);

        mygroups.clear();
        loadData();
        //Log.i("TEST - MY GROUPS:",mygroups.toString());

        cc=new formadapter(mygroups,groupids,getActivity());
        re.setHasFixedSize(true);
        re.setAdapter(cc);
        re.setItemAnimator(new DefaultItemAnimator());
        re.getAdapter().notifyDataSetChanged();

        return view;
    }

    private void loadData() {

        auth=FirebaseAuth.getInstance();

        keyStr = "bbC2H19lkVbQDfakxcrtNMQdd0FloLyw";
        ivStr = "gqLOHUioQ0QjhuvI";
        String userid = auth.getCurrentUser().getUid();
        String usermail=auth.getCurrentUser().getEmail();
        try {
            enmail=ReceivedFragment.encryptStrAndToBase64(ivStr,keyStr,usermail);
            encMail=stringToHex(enmail);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("First", encMail);

        DatabaseReference receivedusrgroup = FirebaseDatabase.getInstance().getReference().child("ReceivedGroupInvitations").child(encMail);

        receivedusrgroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot single:dataSnapshot.getChildren()){
                    String temp=single.getKey().toString();

                    getgrup(temp);
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


    private void getgrup(String temp){
        DatabaseReference groupinfo = FirebaseDatabase.getInstance().getReference().child("Groups").child(temp);
        groupinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("First", dataSnapshot1.child("CreatedTime").getValue().toString());


                if (dataSnapshot.getValue()!=null){
                    String displaytime=dataSnapshot.child("CreatedTime").getValue(String.class);
                    String displayid=dataSnapshot.child("Id").getValue().toString();
                    String displaypath=dataSnapshot.child("ImageUrlPath").getValue().toString();
                    String displayleader=dataSnapshot.child("LeaderId").getValue().toString();
                    String displaytitle=dataSnapshot.child("Title").getValue().toString();
                    mygroupss.add(new Groups(displaytime,displayid,displaypath,displayleader,displaytitle));
                }




                cc.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getgroupinfo(String temp) {

        DatabaseReference groupinfo = FirebaseDatabase.getInstance().getReference().child("ReceivedGroupInvitations").child(encMail).child(temp);
        groupinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("First", dataSnapshot1.child("CreatedTime").getValue().toString());


                if (dataSnapshot.getValue()!=null){
                    String displaystatu=dataSnapshot.child("InvitationStatus").getValue(String.class);
                    Boolean displayisSeen=dataSnapshot.child("IsSeen").getValue(Boolean.class);
                    String displayleader=dataSnapshot.child("LeaderId").getValue(String.class);
                    String displaytime=dataSnapshot.child("SendingTime").getValue(String.class);
                    String displaytitle=dataSnapshot.child("Title").getValue(String.class);
                    Log.e("title", displaytitle);
                    mygroups.add(new Receivedform(displaystatu,displayisSeen,displayleader,displaytime,displaytitle));
                }




                cc.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    public static byte[] encrypt(String ivStr, String keyStr, byte[] bytes) throws Exception{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(ivStr.getBytes());
        byte[] ivBytes = md.digest();

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(keyStr.getBytes());
        byte[] keyBytes = sha.digest();

        return encrypt(ivBytes, keyBytes, bytes);
    }
    public String stringToHex(String input) throws UnsupportedEncodingException
    {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes());
    }
    static byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] bytes) throws Exception{
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(bytes);
    }
    public static String encryptStrAndToBase64(String ivStr, String keyStr, String enStr) throws Exception{
        byte[] bytes = encrypt(keyStr, keyStr, enStr.getBytes("UTF-8"));
        return new String(Base64.encode(bytes ,Base64.DEFAULT), "UTF-8");
    }
    private String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
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
