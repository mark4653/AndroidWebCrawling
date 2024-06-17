package com.example.a20201859_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<PaintTitle> mDataset;


    public MyAdapter(ArrayList<PaintTitle> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public  MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d("hwang", "onCreateViewHolder");


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);  // recyclerview
        // View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewitem, parent, false);  // cardview
        MyViewHolder vh = new  MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //상태, 상품명 표시
        holder.status.setText(mDataset.get(position).status);
        holder.name.setText(mDataset.get(position).name);
        holder.price.setText(mDataset.get(position).price);

        //리스트의 버튼을 누를 시 상품 사이트로 직접 이동
        holder.loadurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).url));
                holder.itemView.getContext().startActivity(intent);
            }
        });
        final Context mycontext = holder.itemView.getContext();



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size(); //
        //  return mDataset.size();
    }



}