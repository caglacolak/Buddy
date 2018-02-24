package com.example.asus.buddy;

/**
 * Created by ASUS on 8.12.2017.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class formadapterv1 extends RecyclerView.Adapter <formadapterv1.ViewHolder> {
    private List<Groups> dataset;

    private String formtype;
    String type;
    Context context;



    public formadapterv1(List<Groups> data) {
        this.dataset=data;

    }

    public formadapterv1(List<Groups> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView groupnametext;
        ImageView groupimage;
        Switch seeOnmap,myLoc;
        private ItemClickListener itemclickListener;

        public ViewHolder(View view) {
            super(view);
            groupnametext=(TextView)view.findViewById(R.id.groupname);
            groupimage=(ImageView)view.findViewById(R.id.groupimage);
            seeOnmap=(Switch)view.findViewById(R.id.switchonmap);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);


        }

        public void setItemclickListener(ItemClickListener itemclickListener){
            this.itemclickListener=itemclickListener;
        }
        @Override
        public void onClick(View v) {
            itemclickListener.onClick(v,getAdapterPosition(),false);

        }

        @Override
        public boolean onLongClick(View v) {
            itemclickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }


    @Override
    public formadapterv1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                parent, false);
        formadapterv1.ViewHolder view_holder = new formadapterv1.ViewHolder(v);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setItemclickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    Toast.makeText(context,"Long Click"+ dataset.get(position),Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent=new Intent(context,ChatActivity.class);

                    intent.putExtra("groupid",dataset.get(position).getGroupid());
                    intent.putExtra("groupname",dataset.get(position).getGroupname());
                    view.getContext().startActivity(intent);
                }
            }
        });

        Groups tiklanilan=dataset.get(position);

        holder.groupnametext.setText(tiklanilan.getGroupname());
        if (tiklanilan.getImageURL().equals(" ")||tiklanilan.getImageURL()==null){
           Log.e("image","empyy");

        }else {
            Log.e("image",tiklanilan.getImageURL());
            Picasso.with(holder.groupimage.getContext()).load(tiklanilan.getImageURL())
                    .placeholder(R.drawable.default_avata).into(holder.groupimage);
        }

        //holder.groupimage.setImageResource(tiklanilan.getImageURL());


    }

    @Override
    public int getItemCount()  {
        return dataset.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

