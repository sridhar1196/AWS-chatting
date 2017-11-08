package com.example.sridh.inclass_09;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThreadDisplay extends AppCompatActivity {
    public SharedPreferences shred;
    public SharedPreferences.Editor edit;
    OkHttpClient client = new OkHttpClient();
    ArrayList<Threads> threadArrayList = new ArrayList<Threads>();
    private RecyclerView mRecyclerView;
    ArrayList<Threads> newThreads = new ArrayList<Threads>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Response responseVal;
    ThreadDisplay threadDisplay;
    Threads threads = new Threads();
    EditText new_thread;
    String username, user_id;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_display);
        shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
        token = shred.getString("Token", "");
        username = shred.getString("UserName", "");
        user_id = shred.getString("Userid", "");
        TextView userName = (TextView) findViewById(R.id.UserName);
        userName.setText(username.trim());
        threadDisplay = ThreadDisplay.this;
        //MyAsyncTask myAsyncTask = new MyAsyncTask();
        Request request = new Request.Builder()
                .header("Authorization", "BEARER " + token)
                .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getBaseContext(), "Error in getting thread", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    threadArrayList = ThreadParser.SignupUtil.parsesignup(response.body().string(), "Get");
                    if(threadArrayList != null){
                        Log.d("chats list:", threadArrayList.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView = (RecyclerView) findViewById(R.id.threadList);
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(ThreadDisplay.this);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mAdapter = new ThreadAdapter(ThreadDisplay.this, threadArrayList, user_id);
                                mRecyclerView.setAdapter(mAdapter);
                                Log.d("demo", "demo");
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error while loading thread", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
                edit = shred.edit();
                edit.clear();
                edit.commit();
                Intent i = new Intent(ThreadDisplay.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        findViewById(R.id.addThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/add";
                new_thread = (EditText) findViewById(R.id.newThread);
                if (new_thread.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "new Thread title is empty", Toast.LENGTH_SHORT).show();
                } else {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("title", new_thread.getText().toString().trim())
                            .build();

                    Log.d("async", "asyncsign" + formBody.toString());
                    final Request request = new Request.Builder()
                            .header("Authorization", "BEARER " + token)
                            .url(url)
                            .post(formBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("demo", "Could not sign up coz " + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                newThreads = ThreadParser.SignupUtil.parsesignup(response.body().string(), "Add");
                                if (newThreads != null) {
                                    threadArrayList.add(newThreads.get(0));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new_thread = (EditText) findViewById(R.id.newThread);
                                            new_thread.setText("");
                                            Toast.makeText(getApplicationContext(), "Thread addition successfull", Toast.LENGTH_SHORT).show();
                                            mRecyclerView = (RecyclerView) findViewById(R.id.threadList);
                                            mRecyclerView.setHasFixedSize(true);
                                            mLayoutManager = new LinearLayoutManager(ThreadDisplay.this);
                                            mRecyclerView.setLayoutManager(mLayoutManager);
                                            mAdapter = new ThreadAdapter(ThreadDisplay.this, threadArrayList, user_id);
                                            mRecyclerView.setAdapter(mAdapter);
                                            Log.d("demo", "demo");
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Error while adding thread", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ThreadDisplay.this, "Error while login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void delete_thread(Threads id, final int position) {
        String url = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/delete/" + String.valueOf(id.getThread_Id());
        Request request = new Request.Builder()
                .header("Authorization", "BEARER " + token)
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getBaseContext(), "Error in deleting thread", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject root = new JSONObject(response.body().string());
                    threadArrayList.remove(position);
                    if (root.has("status") && (root.getString("status").equals("ok"))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Deletion is successfull",Toast.LENGTH_SHORT).show();
                                mRecyclerView = (RecyclerView) findViewById(R.id.threadList);
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(ThreadDisplay.this);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mAdapter = new ThreadAdapter(ThreadDisplay.this, threadArrayList, user_id);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Error while deleting threads",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public  void sendSelectedThread(Threads threads,int position) {

        shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
        edit = shred.edit();
        edit.putString("Token", token);
        edit.putString("UserName", threads.getFname()+" "+threads.getLname());
        edit.putString("Userid", user_id);
        edit.putString("ThreadId",threads.getThread_Id());
        edit.putString("ThreadTitle",threads.getTitle());
        Log.d("thread display",user_id+" "+threads.getFname()+" "+threads.getLname()+" "+threads.getThread_Id()+" "+token);
        edit.commit();
        Intent i = new Intent(ThreadDisplay.this, ChatRoom.class);
        startActivity(i);
    }
//    public void listview(ArrayList<Threads> threadArrayList){
//        mRecyclerView = (RecyclerView) findViewById(R.id.threadList);
//        mRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new ThreadAdapter(ThreadDisplay.this, threadArrayList, user_id);
//        mRecyclerView.setAdapter(mAdapter);
//    }
}