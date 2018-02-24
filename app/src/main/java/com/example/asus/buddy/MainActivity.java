package com.example.asus.buddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

 FirebaseAuth mAuth;


    private Button buttonLogin;
    private Button buttonRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();

        buttonLogin=(Button) findViewById(R.id.btn_login);
        buttonRegister=(Button) findViewById(R.id.btn_register);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(reg);
            }
            });



    }
    protected void onStart(){
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(this,Menu2Activity.class));
        }
    }

    @Override
    public void onClick(View view) {
        if (view==buttonLogin){
            finish();
            Intent log=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(log);
        }
        if(view==buttonRegister){
            Intent reg=new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(reg);
        }


    }


}
