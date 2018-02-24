package com.example.asus.buddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ASUS on 16.11.2017.
 */

public class RegisterSecondActivity  extends AppCompatActivity  {
    private Button buttonRegister;
    private EditText editTextBirthDate;
    private EditText editTextPhoneNumber;
    private EditText editTextCountry;
    private EditText editTextAddress;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private String name,surname,email,password,uri;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondregister);




        progressDialog=new ProgressDialog(this);

        intent=getIntent();
        name=intent.getStringExtra("name");
        surname=intent.getStringExtra("surname");
        email=intent.getStringExtra("email");
        password=intent.getStringExtra("password");
        uri=intent.getStringExtra("uri");



        buttonRegister=(Button) findViewById(R.id.buttonRegister);
        editTextBirthDate=(EditText) findViewById(R.id.editTextBirthdate);
        editTextPhoneNumber=(EditText) findViewById(R.id.editTextPhoneNumber);
        editTextCountry=(EditText) findViewById(R.id.editTextCountry);
        editTextAddress=(EditText) findViewById(R.id.editTextAddress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        auth=FirebaseAuth.getInstance();
    }
    private void registerUser(){
        String bdate=editTextBirthDate.getText().toString().trim();
        String phone=editTextPhoneNumber.getText().toString().trim();
        String country=editTextCountry.getText().toString().trim();
        String address=editTextAddress.getText().toString().trim();


        if (TextUtils.isEmpty(bdate)){
            //bdate is empty
            Toast.makeText(this,"Please enter birthdate",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;
        }
        if (TextUtils.isEmpty(phone)){
            //phone is empty
            Toast.makeText(this,"Please enter phone number",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;

        }
        if (TextUtils.isEmpty(country)){
            //country is empty
            Toast.makeText(this,"Please enter country",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;

        }
        if (TextUtils.isEmpty(address)){
            //address is empty
            Toast.makeText(this,"Please enter address",Toast.LENGTH_SHORT).show();
            //shopping the function execution further
            return;

        }
        progressDialog.setMessage("Registering User...");
        progressDialog.show();


        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(RegisterSecondActivity.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                    String userid=auth.getCurrentUser().getUid();
                    DatabaseReference current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);


                    String bdte=editTextBirthDate.getText().toString();
                    String phne=editTextPhoneNumber.getText().toString();
                    String contry=editTextCountry.getText().toString();
                    String addres=editTextAddress.getText().toString();
                    SimpleDateFormat bicim2=new SimpleDateFormat("yyyy-M-dd");
                    String tarihSaat=bicim2.format(new Date());

                    Map newPost=new HashMap();
                    newPost.put("Address",addres);
                    newPost.put("Birthdate",bdte);
                    newPost.put("Country",contry);
                    newPost.put("Email",email);
                    newPost.put("Id",userid);
                    if(uri!=null){
                        newPost.put("ImageUrlPath",uri);

                    }else {
                        newPost.put("ImageUrlPath"," ");

                    }
                    newPost.put("IsActive",true);
                    newPost.put("Name",name);
                    newPost.put("PhoneNumber",phne);
                    newPost.put("RegisteredTime",tarihSaat.toString());
                    newPost.put("Surname",surname);
                    current_user.updateChildren(newPost);

                    Intent intent = new Intent(RegisterSecondActivity.this, MainActivity.class);
                    startActivity(intent);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Yeni Kullanıcı Hatası", e.getMessage());
            }
        });



    }


}
