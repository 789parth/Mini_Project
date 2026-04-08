package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.domain.categorys;
import com.example.miniproject.R;

import java.util.ArrayList;

public class categoryadapter extends RecyclerView.Adapter<categoryadapter.myviewholder> {

    ArrayList<categorys> list;

    public categoryadapter(ArrayList<categorys> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryitems,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        categorys model = list.get(position);
        if (model == null) return;

        holder.name.setText(model.getCategory_title());
        Glide.with(holder.img.getContext()).load(model.getCategory_image()).into(holder.img);

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class myviewholder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView9);
            name = itemView.findViewById(R.id.textView20);
        }
    }

}
