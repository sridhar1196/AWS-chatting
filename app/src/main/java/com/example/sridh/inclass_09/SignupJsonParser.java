package com.example.sridh.inclass_09;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupJsonParser {

    static public class SignupUtil
    {
        static UserData parsesignup(String in, Context applicationContext) throws JSONException
        {
            JSONObject root = new JSONObject(in);

            if(root.has("status")&&(root.getString("status").equals("ok")))
            {
                Log.d("Token in parser",root.getString("status"));
                UserData user = new UserData();
                user.setToken(root.getString("token"));
                user.setUser_id(root.getString("user_id"));
                user.setFirst_name(root.getString("user_fname"));
                user.setLast_name(root.getString("user_lname"));
                return user;

            }
            return  null;
        }


    }

}

