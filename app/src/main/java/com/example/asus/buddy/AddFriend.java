package com.example.asus.buddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ASUS on 13.01.2018.
 */

public class AddFriend  extends AppCompatActivity {
    private EditText mail;
    private FloatingActionButton addmail;
    ListView list;
    private FirebaseAuth auth;
    Button create;
    String encMail,enmail;
    String ivStr;
    private static final char[] HEX_CHARS = "gqLOHUioQ0QjhuvI".toCharArray();

    String keyStr;
    ArrayList<String> mails;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);
        auth=FirebaseAuth.getInstance();
        mail=(EditText)findViewById(R.id.friendemail);
        list=(ListView)findViewById(R.id.listmails);
        mails=new ArrayList<>();
        addmail=(FloatingActionButton)findViewById(R.id.addbutton);
        loadmails();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,mails);
        adapter.setNotifyOnChange(true);
        list.setAdapter(adapter);
        ((BaseAdapter)adapter).notifyDataSetChanged();
        addmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mails.add(mail.getText().toString());
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,mails);
                adapter.setNotifyOnChange(true);
                list.setAdapter(adapter);
                ((BaseAdapter)adapter).notifyDataSetChanged();
                mail.setText("");
            }
        });





        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder diyalogOlusturucu =
                        new AlertDialog.Builder(view.getContext());

                diyalogOlusturucu.setMessage(mails.get(position))
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mails.remove(position);
                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,mails);
                                adapter.setNotifyOnChange(true);
                                list.setAdapter(adapter);
                                ((BaseAdapter)adapter).notifyDataSetChanged();

                            }
                        });
                diyalogOlusturucu.create().show();
            }
        });

        create=(Button)findViewById(R.id.createbutton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<mails.size();i++){
                    keyStr = "bbC2H19lkVbQDfakxcrtNMQdd0FloLyw";
                    ivStr = "gqLOHUioQ0QjhuvI";
                    String usermail=mails.get(i).toString();
                    SimpleDateFormat bicim2=new SimpleDateFormat("yyyy-M-dd");
                    String tarihSaat=bicim2.format(new Date());
                    try {
                        enmail=CreateSecondGroupFragment.encryptStrAndToBase64(ivStr, keyStr,usermail );
                        encMail=stringToHex(enmail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String userid=auth.getCurrentUser().getUid();
                    DatabaseReference send_group= FirebaseDatabase.getInstance().getReference().child("SentGroupInvitations").child(userid).child(encMail);
                    Map newinvit=new HashMap();
                    newinvit.put("InvitationStatus","Pending");
                    newinvit.put("IsSeen",false);
                    newinvit.put("ReceivedEmail",usermail);
                    newinvit.put("SendingTime",tarihSaat.toString());
                    send_group.updateChildren(newinvit);


                    String groupid = getIntent().getExtras().getString("groupid");
                    String grupname=getIntent().getExtras().getString("groupname");
                    DatabaseReference receive_group= FirebaseDatabase.getInstance().getReference().child("ReceivedGroupInvitations").child(encMail).child(groupid);
                    Map recinvit=new HashMap();
                    recinvit.put("InvitationStatus","Pending");
                    recinvit.put("IsSeen",false);
                    recinvit.put("LeaderId",userid);
                    recinvit.put("SendingTime",tarihSaat.toString());
                    recinvit.put("Title",grupname);
                    receive_group.updateChildren(recinvit);

                    DatabaseReference usrgroup= FirebaseDatabase.getInstance().getReference().child("UsersGroup").child(userid);
                    Map usegrup=new HashMap();
                    usegrup.put(groupid,1);
                    usrgroup.updateChildren(usegrup);
                    DatabaseReference usringroup= FirebaseDatabase.getInstance().getReference().child("UsersInsideGroup").child(groupid);

                    Map grupuser=new HashMap();
                    grupuser.put(userid,1);
                    usringroup.updateChildren(grupuser);




                }


                Intent addfr = new Intent();
                addfr.setClass(AddFriend.this, ChatActivity.class);
                startActivity(addfr);


            }
        });
    }

    private void loadmails() {
        String groupid = getIntent().getExtras().getString("groupid");
        String grupname=getIntent().getExtras().getString("groupname");
        DatabaseReference usringroup= FirebaseDatabase.getInstance().getReference().child("UsersInsideGroup").child(groupid);
        usringroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot single:dataSnapshot.getChildren()){
                    String temp=single.getKey().toString();

                    Log.e("First", single.getKey().toString());
                    getusersmail(temp);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getusersmail(String temp) {
        DatabaseReference usringroups= FirebaseDatabase.getInstance().getReference().child("Users").child(temp);
        usringroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mails.add(dataSnapshot.child("Email").getValue().toString());
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

    static byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] bytes) throws Exception{
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(bytes);
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

    public static String encryptStrAndToBase64(String ivStr, String keyStr, String enStr) throws Exception{
        byte[] bytes = encrypt(keyStr, keyStr, enStr.getBytes("UTF-8"));
        return new String(Base64.encode(bytes ,Base64.DEFAULT), "UTF-8");
    }

    public static String decryptStrAndFromBase64(String ivStr, String keyStr, String deStr) throws Exception{
        byte[] bytes = decrypt(keyStr, keyStr, Base64.decode(deStr.getBytes("UTF-8"),Base64.DEFAULT));
        return new String(bytes, "UTF-8");
    }
    public String stringToHex(String input) throws UnsupportedEncodingException
    {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes());
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

}
