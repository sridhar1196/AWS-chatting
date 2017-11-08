package com.example.sridh.inclass_09;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sridh on 11/6/2017.
 */

public class ThreadParser {
    static public class SignupUtil
    {
        static ArrayList<Threads> parsesignup(String in, String add) throws JSONException {
            //Log.d("demo",in);
            if (add.equals("Get")) {
                JSONObject root = new JSONObject(in);
                ArrayList<Threads> threads = new ArrayList<Threads>();
                if (root.has("status") && (root.getString("status").equals("ok"))) {
                    JSONArray array = root.getJSONArray("threads");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Threads thread = new Threads();
                        thread.setFname(obj.getString("user_fname"));
                        thread.setLname(obj.getString("user_lname"));
                        thread.setUser_id(obj.getString("user_id"));
                        thread.setThread_Id(obj.getString("id"));
                        thread.setTitle(obj.getString("title"));
                        threads.add(thread);
                    }
                    return threads;

                }
            } else {
                JSONObject root = new JSONObject(in);
                Log.d("demo",root.toString());
                ArrayList<Threads> threads = new ArrayList<Threads>();
                if (root.has("status") && (root.getString("status").equals("ok"))) {
                    JSONObject obj = root.getJSONObject("thread");
                    Threads thread = new Threads();
                    thread.setFname(obj.getString("user_fname"));
                    thread.setLname(obj.getString("user_lname"));
                    thread.setUser_id(obj.getString("user_id"));
                    thread.setThread_Id(obj.getString("id"));
                    thread.setTitle(obj.getString("title"));
                    threads.add(thread);
                    return threads;
                }
            }
            return null;
        }
    }
}
