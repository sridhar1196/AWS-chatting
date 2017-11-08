package com.example.sridh.inclass_09;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

public class SignUp extends AppCompatActivity {
    String token;
    UserData userData = new UserData();
    public SharedPreferences shred;
    public SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email_signup);
                EditText ch_pwd = (EditText) findViewById(R.id.choose_password);
                EditText re_pwd = (EditText) findViewById(R.id.repeat_password);
                EditText f_name = (EditText) findViewById(R.id.first_name);
                EditText l_name = (EditText) findViewById(R.id.last_name);

                String url = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/signup";
                if(f_name.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter First Name ",Toast.LENGTH_SHORT).show();
                } else if(l_name.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter Last Name ",Toast.LENGTH_SHORT).show();
                } else if(email.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter email id ",Toast.LENGTH_SHORT).show();
                } else if(ch_pwd.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter Check Password ",Toast.LENGTH_SHORT).show();
                } else if(re_pwd.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter Retype Password ",Toast.LENGTH_SHORT).show();
                } else if(!(ch_pwd.getText().toString().trim().equals(re_pwd.getText().toString().trim()))){
                    Toast.makeText(SignUp.this,"Password are not similar",Toast.LENGTH_SHORT).show();
                } else {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("email", email.getText().toString().trim())
                            .add("password", ch_pwd.getText().toString().trim())
                            .add("fname", f_name.getText().toString().trim())
                            .add("lname", l_name.getText().toString().trim())
                            .build();

                    Log.d("async", "asyncsign" + formBody.toString());
                    final Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("demo","Could not sign up coz "+e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                userData = SignupJsonParser.SignupUtil.parsesignup(response.body().string(),getApplicationContext());
                                if(userData!=null) {
                                    Log.d("signed Up",userData.toString());
                                    shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
                                    edit = shred.edit();
                                    edit.putString("Token", userData.getToken());
                                    edit.putString("UserName", userData.getFirst_name() + " " + userData.getLast_name());
                                    edit.putString("Userid", userData.getUser_id());
                                    edit.commit();
                                    Intent i = new Intent(SignUp.this, ThreadDisplay.class);
                                    startActivity(i);
                                }
                                else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getBaseContext(),"Could not sign up",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                Toast.makeText(SignUp.this,"Error while login",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
