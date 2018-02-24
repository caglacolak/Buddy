package com.example.asus.buddy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class formadaptersend extends RecyclerView.Adapter <formadaptersend.ViewHolder> {
    private List<Sendform> dataset;

    private String formtype;
    String type;
    Context context;

    List<String> idgrups;



    public formadaptersend(List<Sendform> data, CustomItemClickListener customItemClickListener) {
        this.dataset=data;

    }

    public formadaptersend(List<Sendform> dataset, List<String> groupids, Context context) {
        this.idgrups=groupids;
        this.dataset = dataset;
        this.context = context;
    }
    CustomItemClickListener listener;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mDeleteImage;
        TextView groupnametext,usermail,status;
        ImageView groupimage;
        CustomItemClickListener listener;


        public ViewHolder(View view) {
            super(view);
            groupnametext=(TextView)view.findViewById(R.id.groupname);
            usermail=(TextView)view.findViewById(R.id.usermail);
            status=(TextView)view.findViewById(R.id.status);
            groupimage=(ImageView)view.findViewById(R.id.groupimage);
            mDeleteImage=(ImageView)view.findViewById(R.id.delete);




        }

    }


    @Override
    public formadaptersend.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send,
                parent, false);
        final ViewHolder view_holder = new ViewHolder(v);


        return view_holder;
    }

    String keyStr,enmail,encMail;
    String ivStr;
    private static final char[] HEX_CHARS = "gqLOHUioQ0QjhuvI".toCharArray();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    public void onBindViewHolder(final formadaptersend.ViewHolder holder, final int position) {

        final Sendform tiklanilan=dataset.get(position);

        holder.usermail.setText(tiklanilan.getReceivedEmail());
        holder.status.setText(tiklanilan.getInvitationStatus());

        //holder.groupimage.setImageResource(tiklanilan.getImageURL());

        holder.mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                keyStr = "bbC2H19lkVbQDfakxcrtNMQdd0FloLyw";
                ivStr = "gqLOHUioQ0QjhuvI";
                auth= FirebaseAuth.getInstance();
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


                Log.e("denemeeee",idgrups.get(position).toString());
                DatabaseReference sendgroup = FirebaseDatabase.getInstance().getReference().child("SentGroupInvitations").child(userid).child(idgrups.get(position).toString());
                sendgroup.removeValue();
                sendgroup.removeEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.e("delete","deleted");
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                remove(dataset.get(position));
                Log.i("DECLİNE",tiklanilan.getReceivedEmail());
            }
        });

    }

    @Override
    public int getItemCount()  {
        return dataset.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public void remove(Sendform data) {

        int position = dataset.indexOf(data);
        dataset.remove(position);
        notifyItemRemoved(position);
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