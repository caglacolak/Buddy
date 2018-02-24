package com.example.asus.buddy;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateSecondGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateSecondGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSecondGroupFragment extends Fragment {
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CreateSecondGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateSecondGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateSecondGroupFragment newInstance(String param1, String param2) {
        CreateSecondGroupFragment fragment = new CreateSecondGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_second_group, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context,"ChatFragment Atteched",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        mail=(EditText)getActivity().findViewById(R.id.friendemail);
        list=(ListView)getActivity().findViewById(R.id.listmails);
        mails=new ArrayList<>();
        addmail=(FloatingActionButton)getActivity().findViewById(R.id.addbutton);
        addmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mails.add(mail.getText().toString());
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,mails);
                adapter.setNotifyOnChange(true);
                list.setAdapter(adapter);
                ((BaseAdapter)adapter).notifyDataSetChanged();
                mail.setText("");
            }
        });





       list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder diyalogOlusturucu =
                        new AlertDialog.Builder(getContext());

                diyalogOlusturucu.setMessage(mails.get(position))
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mails.remove(position);
                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,mails);
                                adapter.setNotifyOnChange(true);
                                list.setAdapter(adapter);
                                ((BaseAdapter)adapter).notifyDataSetChanged();

                            }
                        });
                diyalogOlusturucu.create().show();
            }
        });

       create=(Button)getActivity().findViewById(R.id.createbutton);
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


                    Bundle arguments = getArguments();
                    String groupid = arguments.getString("gruopid");
                    String grupname=arguments.getString("gruopname");
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
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.content,new ChatFragment()).commit();





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
