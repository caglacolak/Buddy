package com.example.asus.buddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ASUS on 13.01.2018.
 */

public class EditGroupActivity  extends AppCompatActivity {
    private ImageView image;
    private static final int RESULT_LOAD_IMAGE=1;
    Uri sellectedImage;
    EditText groupnme;
    private StorageReference mStorageRef;
    private CreateGroupFragment.OnFragmentInteractionListener mListener;
    private FirebaseAuth auth;
    String generatedFilePath;
    String dCreatedTime;

    String Id;
    String ImageUrlPath;
    String LeaderId;
    String Title;
    Button nextbut;
    Button imageb;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        auth=FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        image=(ImageView)findViewById(R.id.imageView);
        groupnme=(EditText)findViewById(R.id.groupnamecreate);
        imageb=(Button)findViewById(R.id.buttonImage);

        imageb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galeryIntent,RESULT_LOAD_IMAGE);

            }
        });

        String groupid= getIntent().getExtras().getString("groupid");
        DatabaseReference current_grup= FirebaseDatabase.getInstance().getReference().child("Groups").child(groupid);
        current_grup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dCreatedTime=dataSnapshot.child("CreatedTime").getValue().toString();
                Id=dataSnapshot.child("Id").getValue().toString();
                ImageUrlPath=dataSnapshot.child("ImageUrlPath").getValue().toString();
                LeaderId=dataSnapshot.child("LeaderId").getValue().toString();
                Title=dataSnapshot.child("Title").getValue().toString();
                Log.e("title",Title);
                groupnme.setText(Title);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (ImageUrlPath!=null){
            image.setImageURI(Uri.parse(ImageUrlPath));

        }
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.nextbutton);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name=groupnme.getText().toString().trim();


                if (TextUtils.isEmpty(name)){
                    //name is empty
                    //shopping the function execution further
                    return;
                }

                String groupid= getIntent().getExtras().getString("groupid");

                DatabaseReference current_grup= FirebaseDatabase.getInstance().getReference().child("Groups").child(groupid);

                String userid=auth.getCurrentUser().getUid();
                Map newPost=new HashMap();
                newPost.put("CreatedTime",dCreatedTime);
                newPost.put("Id",Id);
                if (generatedFilePath!=null){
                    newPost.put("ImageUrlPath",generatedFilePath.toString());
                }else {
                    newPost.put("ImageUrlPath",ImageUrlPath);

                }

                newPost.put("LeaderId",LeaderId);
                newPost.put("Title",groupnme.getText().toString());
                current_grup.updateChildren(newPost);

                Intent addfr = new Intent();
                addfr.setClass(EditGroupActivity.this, ChatActivity.class);
                startActivity(addfr);

            }
        });


    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==RESULT_LOAD_IMAGE&&data!=null){
            sellectedImage=data.getData();
            image.setImageURI(sellectedImage);
            if(sellectedImage!=null){
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
}
