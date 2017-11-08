package com.example.sridh.inclass_09;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatRoom extends AppCompatActivity {
    public SharedPreferences shred;
    public SharedPreferences.Editor edit;
    OkHttpClient client = new OkHttpClient();
    ArrayList<Message> messageArrayList = new ArrayList<Message>();
    ArrayList<Message> new_message = new ArrayList<Message>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Response responseVal;
    ThreadDisplay threadDisplay;
    String username, user_id;
    String token;
    String thread_id;
    TextView threadName;
    EditText chat_message;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        shred = getSharedPreferences("Fav", Context.MODE_PRIVATE);
        token = shred.getString("Token", "");
        username = shred.getString("UserName", "");
        user_id = shred.getString("Userid", "");
        thread_id=shred.getString("ThreadId","");
        title = shred.getString("ThreadTitle","");
        threadName = (TextView) findViewById(R.id.thread_title);
        threadName.setText(title.trim());
        Log.d("chatroom",thread_id);
        Log.d("chatroom",token);
        Request request = new Request.Builder()
                .header("Authorization", "BEARER " +token)
                .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/messages/"+thread_id)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ChatRoom.this, "Error in getting thread", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    Log.d("reponse",response.toString());
                    messageArrayList = ChatsParser.SignupUtil.parsesignup(response.body().string(),"Get");
                    if(messageArrayList != null){
                        Log.d("chats list:", messageArrayList.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView = (RecyclerView) findViewById(R.id.chatList);
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(ChatRoom.this);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mAdapter = new ChatAdapter(ChatRoom.this, messageArrayList, user_id);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Error while fetching chat",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatRoom.this, ThreadDisplay.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        findViewById(R.id.sendChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/add";
                chat_message = (EditText) findViewById(R.id.chat_message);
                if (chat_message.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "new chat is empty", Toast.LENGTH_SHORT).show();
                } else {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("message", chat_message.getText().toString().trim())
                            .add("thread_id", thread_id)
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
                                new_message = ChatsParser.SignupUtil.parsesignup(response.body().string(),"Add");
                                if(new_message != null){
                                    messageArrayList.add(new_message.get(0));
                                    Log.d("chats list:", messageArrayList.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chat_message = (EditText) findViewById(R.id.chat_message);
                                            chat_message.setText("");
                                            mRecyclerView = (RecyclerView) findViewById(R.id.chatList);
                                            mRecyclerView.setHasFixedSize(true);
                                            mLayoutManager = new LinearLayoutManager(ChatRoom.this);
                                            mRecyclerView.setLayoutManager(mLayoutManager);
                                            mAdapter = new ChatAdapter(ChatRoom.this, messageArrayList, user_id);
                                            mRecyclerView.setAdapter(mAdapter);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Error while fetching chat",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error while login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void delete_chat(Message id, final int position) {
        String url = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/delete/" + String.valueOf(id.getId());
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
                    messageArrayList.remove(position);
                    if (root.has("status") && (root.getString("status").equals("ok"))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Deletion is successfull", Toast.LENGTH_SHORT).show();
                                mRecyclerView = (RecyclerView) findViewById(R.id.chatList);
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(ChatRoom.this);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mAdapter = new ChatAdapter(ChatRoom.this, messageArrayList, user_id);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Error while removing chat",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}