package com.example.sridh.inclass_09;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String token;
    UserData userData = new UserData();
    public SharedPreferences shred;
    public SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignUp.class);
                i.putExtra("temp", "signup");
                startActivity(i);
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email);
                EditText pwd = (EditText) findViewById(R.id.password);
                String url = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/login";
                if(email.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter email id ",Toast.LENGTH_SHORT).show();
                } else if(pwd.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter password ",Toast.LENGTH_SHORT).show();
                } else{
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("email", email.getText().toString().trim())
                            .add("password", pwd.getText().toString().trim())
                            .build();

                    final Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("demo","faliure"+e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                userData = SignupJsonParser.SignupUtil.parsesignup(response.body().string(),getApplicationContext());
//                            callThread();
                                if(userData != null){
                                    shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
                                    edit = shred.edit();
                                    edit.putString("Token", userData.getToken());
                                    edit.putString("UserName", userData.getFirst_name()+" "+userData.getLast_name());
                                    edit.putString("Userid",userData.getUser_id());
                                    edit.commit();
                                    Intent i = new Intent(MainActivity.this,ThreadDisplay.class);
                                    startActivity(i);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Error while login",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(),"Error while login",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

}
