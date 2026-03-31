package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.R;
import com.example.miniproject.domain.products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class productadapter extends FirebaseRecyclerAdapter<products,productadapter.myviewholder>
{
    public productadapter(@NonNull FirebaseRecyclerOptions<products> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull products model) {
        holder.name.setText(model.getName());
        holder.quantity.setText(model.getQuntity());
        holder.price.setText(String.valueOf(model.getPrice()));
        Glide.with(holder.img.getContext()).load(model.getImagePath()).into(holder.img);
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productitems,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView img,addIcon;
        TextView name,quantity,price;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.imageView8);
            //addIcon=itemView.findViewById(R.id.imageView1);
            name=itemView.findViewById(R.id.textView18);
            quantity=itemView.findViewById(R.id.textView15);
            price=itemView.findViewById(R.id.textView24);
        }
    }
}
