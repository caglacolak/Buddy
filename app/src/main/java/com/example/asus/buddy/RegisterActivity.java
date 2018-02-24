package com.example.asus.buddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

/**
 * Created by ASUS on 16.11.2017.
 */

public class RegisterActivity extends AppCompatActivity {
    private Button buttonNext;
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ImageView image;
    private static final int RESULT_LOAD_IMAGE=1;
    Uri sellectedImage;
    private StorageReference mStorageRef;
    Uri downloadUrl;
    String generatedFilePath;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        mStorageRef= FirebaseStorage.getInstance().getReference();

        image=(ImageView) findViewById(R.id.imageView);

        editTextName=(EditText) findViewById(R.id.editTextName);
        editTextSurname=(EditText) findViewById(R.id.editTextSurname);
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }


    public void uploadImage(View view) {

        Intent galeryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeryIntent,RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==RESULT_LOAD_IMAGE&&resultCode==RESULT_OK&&data!=null){
            sellectedImage=data.getData();
            image.setImageURI(sellectedImage);
            if(sellectedImage!=null){
                String imgname= UUID.randomUUID().toString();
                StorageReference storageReference=mStorageRef.child("UserImages/"+imgname+".jpg");
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

    public void nextButton(View view) {
        String name=editTextName.getText().toString().trim();
        String surname=editTextSurname.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            //name is empty
            Toast.makeText(this,"Please enter yourname",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;
        }
        if (TextUtils.isEmpty(surname)){
            //surname is empty
            Toast.makeText(this,"Please enter your surname",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;

        }
        if (TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;

        }
        if (TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;

        }
        Intent intocan=new Intent();
        intocan.putExtra("name",name);
        intocan.putExtra("surname",surname);
        intocan.putExtra("email",email);
        intocan.putExtra("password",password);
        intocan.putExtra("uri",generatedFilePath);
        intocan.setClass(RegisterActivity.this,RegisterSecondActivity.class);
        startActivity(intocan);


    }
}

