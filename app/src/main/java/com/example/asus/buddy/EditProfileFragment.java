package com.example.asus.buddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    EditText nametex,surname,mail,password,phone,addres,birthdate;
    Button changebut,editbut;
    private static final int RESULT_LOAD_IMAGE=1;
    Uri sellectedImage;
    String userid;
    String generatedFilePath;
    String displayname;
    String displaysurname;
    String displaymail;
    String displayphone;
    String displayadress;
    String displaybdate;
    String displayUrl,displaycountry,displaytime;


    private ImageView image;

    private StorageReference mStorageRef;
    private FirebaseAuth auth;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==RESULT_LOAD_IMAGE&&data!=null){
            sellectedImage=data.getData();
            if(sellectedImage!=null){
                image.setImageURI(sellectedImage);
                String imgname= UUID.randomUUID().toString();
                StorageReference storageReference=mStorageRef.child("GroupImages/"+imgname+".jpg");
                storageReference.putFile(sellectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                        generatedFilePath = downloadUri.toString();

                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_edit_profile, container, false);


        return view;
    }
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        nametex=(EditText)getActivity().findViewById(R.id.name);
        surname=(EditText)getActivity().findViewById(R.id.surname);
        mail=(EditText)getActivity().findViewById(R.id.mail);
        password=(EditText)getActivity().findViewById(R.id.passwords);
        image=(ImageView)getActivity().findViewById(R.id.imageView4);
        auth= FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        phone=(EditText)getActivity().findViewById(R.id.phone);
        addres=(EditText)getActivity().findViewById(R.id.addresss);
        birthdate=(EditText)getActivity().findViewById(R.id.bdate);
        userid=auth.getCurrentUser().getUid();
        final DatabaseReference current_grup= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        current_grup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                displayname=dataSnapshot.child("Name").getValue().toString();
                displaysurname=dataSnapshot.child("Surname").getValue().toString();
                displaymail=dataSnapshot.child("Email").getValue().toString();
                displayphone=dataSnapshot.child("PhoneNumber").getValue().toString();
                displayadress=dataSnapshot.child("Address").getValue().toString();
                displaybdate=dataSnapshot.child("Birthdate").getValue().toString();
                if(dataSnapshot.child("ImageUrlPath").getValue()!=null&&dataSnapshot.child("ImageUrlPath").getValue()!=" "){
                    displayUrl = dataSnapshot.child("ImageUrlPath").getValue().toString();

                }
                displaycountry = dataSnapshot.child("Country").getValue().toString();

                displaytime = dataSnapshot.child("RegisteredTime").getValue().toString();


                if(displayUrl==null||displayUrl.equals(" ")){

                }else {
                    Picasso.with(image.getContext()).load(displayUrl)
                            .placeholder(R.drawable.default_avata).into(image);

                }



                nametex.setText(displayname);
                surname.setText(displaysurname);
                mail.setText(displaymail);
                phone.setText(displayphone);
                addres.setText(displayadress);
                birthdate.setText(displaybdate);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        changebut=(Button)getActivity().findViewById(R.id.change);
        editbut=(Button)getActivity().findViewById(R.id.editbut);
        changebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galeryIntent,RESULT_LOAD_IMAGE);

            }
        });

        editbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid=auth.getCurrentUser().getUid();

                DatabaseReference current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                Map newPost=new HashMap();
                newPost.put("Address",addres.getText().toString());
                newPost.put("Birthdate",birthdate.getText().toString());
                newPost.put("Country",displaycountry);
                newPost.put("Email",mail.getText().toString());
                newPost.put("Id",userid);
                if (generatedFilePath!=displayUrl){
                    newPost.put("ImageUrlPath",generatedFilePath);

                }else {
                    newPost.put("ImageUrlPath",displayUrl);

                }
                newPost.put("IsActive",true);
                newPost.put("Name",nametex.getText().toString());
                newPost.put("PhoneNumber",phone.getText().toString());
                newPost.put("RegisteredTime",displaytime);
                newPost.put("Surname",surname.getText().toString());
                current_user.updateChildren(newPost);







            }
        });




    }




}
