package com.example.sridh.inclass_09;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sridh on 11/7/2017.
 */
public class ChatsParser {

    static public class SignupUtil {
        static ArrayList<Message> parsesignup(String in, String add) throws JSONException {
            //Log.d("demo",in);
            JSONObject root = new JSONObject(in);
            if (add.equals("Get")) {
                Log.d("root",root.toString());
                ArrayList<Message> threads = new ArrayList<Message>();
                if (root.has("status") && (root.getString("status").equals("ok"))) {
                    JSONArray array = root.getJSONArray("messages");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Message thread = new Message();
                        thread.setFname(obj.getString("user_fname"));
                        thread.setLname(obj.getString("user_lname"));
                        thread.setUser_id(obj.getString("user_id"));
                        thread.setId(obj.getString("id"));
                        thread.setMessage(obj.getString("message"));
                        thread.setCreated_at(obj.getString("created_at"));
                        Log.d("threads",thread.toString());
                        threads.add(thread);
                    }
                    return threads;

                }
            } else {
                Log.d("root",root.toString());
                ArrayList<Message> threads = new ArrayList<Message>();
                if (root.has("status") && (root.getString("status").equals("ok"))) {
                    JSONObject obj = root.getJSONObject("message");
                    Message thread = new Message();
                    thread.setFname(obj.getString("user_fname"));
                    thread.setLname(obj.getString("user_lname"));
                    thread.setUser_id(obj.getString("user_id"));
                    thread.setId(obj.getString("id"));
                    thread.setMessage(obj.getString("message"));
                    thread.setCreated_at(obj.getString("created_at"));
                    Log.d("threads",thread.toString());
                    threads.add(thread);
                    return threads;

                }
            }
            return null;
        }

    }
}