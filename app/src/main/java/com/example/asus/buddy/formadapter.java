package com.example.asus.buddy;

/**
 * Created by ASUS on 8.12.2017.
 */
import android.content.Context;
import android.location.LocationManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class formadapter  extends RecyclerView.Adapter <formadapter.ViewHolder> {

    private List<Receivedform> dataset;

    private List<String> idsgroup;
    private setItemClickListener mListener;
    private String formtype;
    String type;
    Context context;

    public void setOnItemClickListener(setItemClickListener listener){
        mListener=listener;
    }
    public formadapter(List<Receivedform> data, setItemClickListener setItemClickListener) {
        this.dataset=data;

    }

    public formadapter(List<Receivedform> dataset, List<String> groupids, Context context) {
        this.idsgroup=groupids;
        this.dataset = dataset;
        this.context = context;
    }
    setItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        setItemClickListener listener;

        TextView groupnametext;
        ImageView groupimage;
        Button accept,reject;

        public ViewHolder(View view) {
            super(view);
            groupnametext=(TextView)view.findViewById(R.id.groupname);
            groupimage=(ImageView)view.findViewById(R.id.groupimage);
            accept=(Button)view.findViewById(R.id.buttonaccept);
            reject=(Button)view.findViewById(R.id.buttonreject);

            final int position=getAdapterPosition();

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null ){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onAcceptClick(v,position);
                        }
                    }



                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null ){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onRejectClick(v,position);
                        }
                    }

                }
            });


        }





    }

    String keyStr,enmail,encMail;
    String ivStr;
    private static final char[] HEX_CHARS = "gqLOHUioQ0QjhuvI".toCharArray();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    @Override
    public formadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2,
                parent, false);
        formadapter.ViewHolder view_holder = new formadapter.ViewHolder(v);
        final ViewHolder iew_holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAcceptClick(v, iew_holder.getAdapterPosition());
            }
        });
        return view_holder;
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Receivedform tiklanilan=dataset.get(position);

        holder.groupnametext.setText(tiklanilan.getTitle());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        Log.e("error","accept");
                        accept(position);

                        remove(dataset.get(position));


            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        reject(position);
                        remove(dataset.get(position));


            }
        });



        //holder.groupimage.setImageResource(tiklanilan.getImageURL());


    }
    public void remove(Receivedform data) {
        int position = dataset.indexOf(data);
        dataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataset.size());

    }
    @Override
    public int getItemCount()  {
        return dataset.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    private void accept(int position){
        keyStr = "bbC2H19lkVbQDfakxcrtNMQdd0FloLyw";
        ivStr = "gqLOHUioQ0QjhuvI";
        auth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userid = auth.getCurrentUser().getUid();
        Log.e("user",userid);
        String usermail=auth.getCurrentUser().getEmail();

        try {
            String enmail=encryptStrAndToBase64(ivStr,keyStr,usermail);

            encMail=stringToHex(enmail);
            Log.e("encmail",encMail);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String temp=idsgroup.get(position).toString();
        Log.e("temp",temp);
        DatabaseReference receivedusrgroup = FirebaseDatabase.getInstance().getReference().child("ReceivedGroupInvitations").child(encMail).child(idsgroup.get(position).toString());

        DatabaseReference usersgroup = FirebaseDatabase.getInstance().getReference().child("UsersGroup").child(userid);
        DatabaseReference usringroup= FirebaseDatabase.getInstance().getReference().child("UsersInsideGroup").child(idsgroup.get(position).toString());
        DatabaseReference sendgroup = FirebaseDatabase.getInstance().getReference().child("SentGroupInvitations").child(dataset.get(position).getLeaderId()).child(encMail);
        Log.e("deneme","receive");
        Map sendPost=new HashMap();
        sendPost.put("InvitationStatus","Accept");
        sendPost.put("IsSeen",true);
        sendPost.put("ReceivedEmail",usermail);
        sendPost.put("SendingTime",dataset.get(position).getSendingTime().toString());
        sendgroup.updateChildren(sendPost);


        Map usegrup=new HashMap();
        usegrup.put(idsgroup.get(position).toString(),1);
        usersgroup.updateChildren(usegrup);

        Map grupuser=new HashMap();
        grupuser.put(userid,1);
        usringroup.updateChildren(grupuser);
        receivedusrgroup.removeValue();
        receivedusrgroup.removeEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("delete",dataSnapshot.getKey().toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void reject(int position){
        keyStr = "bbC2H19lkVbQDfakxcrtNMQdd0FloLyw";
        ivStr = "gqLOHUioQ0QjhuvI";
        auth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userid = auth.getCurrentUser().getUid();
        String usermail=auth.getCurrentUser().getEmail();
        try {
            String enmail=ReceivedFragment.encryptStrAndToBase64(ivStr,keyStr,usermail);
            encMail=stringToHex(enmail);

        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseReference sendgroup = FirebaseDatabase.getInstance().getReference().child("SentGroupInvitations").child(dataset.get(position).getLeaderId()).child(encMail);
        Log.e("receiveedgroupl",idsgroup.get(position).toString());
        DatabaseReference receivedusrgroup = FirebaseDatabase.getInstance().getReference().child("ReceivedGroupInvitations").child(encMail).child(idsgroup.get(position).toString());


        Map sendPost=new HashMap();
        sendPost.put("InvitationStatus","Reject");
        sendPost.put("IsSeen",true);
        sendPost.put("ReceivedEmail",usermail);
        sendPost.put("SendingTime",dataset.get(position).getSendingTime().toString());
        sendgroup.updateChildren(sendPost);
        receivedusrgroup.removeValue();

        receivedusrgroup.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("deleted","deleeeete");
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

