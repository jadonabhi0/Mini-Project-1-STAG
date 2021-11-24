package com.example.stag.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stag.Models.Messages;
import com.example.stag.R;
import com.example.stag.databinding.ActivityChattingDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class Chatdapter extends RecyclerView.Adapter {


    ArrayList<Messages > messagemodel;
    Context context;
    String recId;

    ActivityChattingDetailsBinding binding;


    public Chatdapter(ArrayList<Messages> messagemodel, Context context) {
        this.messagemodel = messagemodel;
        this.context = context;
    }

    public Chatdapter(ArrayList<Messages> messagemodel, Context context, String recId) {
        this.messagemodel = messagemodel;
        this.context = context;
        this.recId = recId;
    }

    int SENDER_VIEW_TYPE = 1;
    int RECIVER_VIEW_TYPE = 2 ;



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == SENDER_VIEW_TYPE){
            View view  = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderRoom(view);
        }
        else{
            View  view  = LayoutInflater.from(context).inflate(R.layout.sample_reciver,parent,false);
            return new ReciverRoom(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (messagemodel.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECIVER_VIEW_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        Messages messagesmodel = messagemodel.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure  to delete the message")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String sender = FirebaseAuth.getInstance().getUid() + recId;

                                database.getReference().child("Chats").child(sender).child(messagesmodel.getMessageid())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }
        });




        if(holder.getClass() == SenderRoom.class){
            ((SenderRoom) holder).sendermessage.setText(messagesmodel.getMessage());
            ImageView imageView;

            if (messagesmodel.getMessage().equals("photo")){
//                holder.itemView.findViewById(R.id.senderimage).setVisibility(View.VISIBLE);
//                holder.itemView.findViewById(R.id.sendertext).setVisibility(View.GONE);
//                Picasso.get().load(messagesmodel.getImageurl()).placeholder(R.drawable.placeholde).into((ImageView) holder.itemView.findViewById(R.id.senderimage));

                ((SenderRoom) holder).senderimage.setVisibility(View.VISIBLE);
                ((SenderRoom) holder).sendermessage.setVisibility(View.GONE);
//                Picasso.get().load(messagesmodel.getImageurl()).placeholder(R.drawable.placeholde).into((ImageView) holder.itemView.findViewById(R.id.senderimage));

            }
        }
        else {
            ((ReciverRoom)holder).recivermessage.setText(messagesmodel.getMessage());
        }



    }

    @Override
    public int getItemCount() {
        return messagemodel.size() ;
    }




    public class ReciverRoom extends RecyclerView.ViewHolder{

    TextView recivermessage, recivertime;

    public ReciverRoom(@NonNull View itemView) {
        super(itemView);


        recivermessage = itemView.findViewById(R.id.recivertext);
        recivertime = itemView.findViewById(R.id.recivertime);


    }
}

    public class SenderRoom extends RecyclerView.ViewHolder{

        TextView sendermessage, sendertime,senderimage;

        public SenderRoom(@NonNull View itemView) {
            super(itemView);


            sendermessage = itemView.findViewById(R.id.sendertext);
            sendertime = itemView.findViewById(R.id.sendertime);
//            senderimage = itemView.findViewById(R.id.senderimage);

        }
    }



}
