
package com.example.sridh.inclass_09;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by BhaBhaHP on 11/6/2017.
 */

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder>  {

    ArrayList<Threads> threadArrayList=new ArrayList<Threads>();
    Context mContext;
    ThreadDisplay threadDisplay;
    String user_id;
    public ThreadAdapter(ThreadDisplay context, ArrayList<Threads> threadList, String user_id)
    {

        mContext=context;
        this.threadDisplay = context;
        this.threadArrayList=threadList;
        this.user_id = user_id;
        Log.d("constructor",threadList.toString());
    }

    @Override
    public ThreadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.threadlist, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ThreadAdapter.ViewHolder holder, int position) {
        holder.threadName.setText(threadArrayList.get(position).getTitle().trim());
        if(threadArrayList.get(position).getUser_id().trim().equals(user_id.trim())){
            holder.delete_thread.setVisibility(View.VISIBLE);
            holder.delete_thread.setEnabled(true);
        } else {
            holder.delete_thread.setVisibility(View.INVISIBLE);
            holder.delete_thread.setEnabled(false);
        }
        holder.position = position;
        holder.context = mContext;
        holder.threadDisplay = threadDisplay;
        holder.threads = threadArrayList;
    }

    @Override
    public int getItemCount() {
        if(threadArrayList != null){
            return threadArrayList.size();
        } else {
            return 0;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView threadName;
        ImageView delete_thread;
        int position;
        Context context;
        ThreadDisplay threadDisplay;
        ArrayList<Threads> threads;
        public ViewHolder(View itemView) {
            super(itemView);
            threadName = (TextView) itemView.findViewById(R.id.threadName);
            delete_thread = (ImageView) itemView.findViewById(R.id.delete_thread);
            delete_thread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    threadDisplay.delete_thread(threads.get(position),position);
                    ThreadAdapter.this.notifyDataSetChanged();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    threadDisplay.sendSelectedThread(threadArrayList.get(position),position);
                }
            });
        }
    }
}
