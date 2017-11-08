package com.example.sridh.inclass_09;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sridh on 11/7/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<com.example.sridh.inclass_09.ChatAdapter.ViewHolder>  {

    ArrayList<Message> threadArrayList=new ArrayList<Message>();
    ChatRoom mContext;
    String user_id;
    public ChatAdapter(ChatRoom context, ArrayList<Message> threadList, String user_id)
    {

        mContext=context;
        this.threadArrayList=threadList;
        this.user_id = user_id;
        Log.d("constructor",threadList.toString());
    }

    View view;
    @Override
    public com.example.sridh.inclass_09.ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatlist, parent, false);

        return new com.example.sridh.inclass_09.ChatAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(com.example.sridh.inclass_09.ChatAdapter.ViewHolder holder, final int position) {
        holder.message.setText(threadArrayList.get(position).getMessage());
        if(threadArrayList.get(position).getUser_id().trim().equals(user_id.trim())){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setEnabled(true);
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
            holder.delete.setEnabled(false);
        }
        holder.FullName.setText(threadArrayList.get(position).getFname().trim() +" "+threadArrayList.get(position).getLname().trim());
        holder.position = position;
        holder.chatRoom = mContext;
        holder.threads = threadArrayList;

        PrettyTime p = new PrettyTime();
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date startDate = null;
        try{
            startDate = dateformat.parse(threadArrayList.get(position).getCreated_at());
        } catch (ParseException e){
            e.printStackTrace();
        }
        holder.time.setText(p.format(startDate));
//        //prints: “moments from now”
//
//        System.out.println(p.format(new Date(System.currentTimeMillis() + 1000*60*10)));
//        //prints: “10 minutes from now”


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
        TextView message;
        TextView time,FullName;
        ImageView delete;
        int position;
        Context context;
        ChatRoom chatRoom;
        ArrayList<Message> threads;
        public ViewHolder(View itemView) {
            super(itemView);

            delete = (ImageView) itemView.findViewById(R.id.deleteMessage);
            message=(TextView) itemView.findViewById(R.id.message);
            FullName = (TextView) itemView.findViewById(R.id.FullName);
            time = (TextView) itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatRoom.delete_chat(threads.get(position),position);
                    ChatAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}


