package com.example.a20201859_project;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView status, name, price;
    Button loadurl;

    //itemview 설정
    public MyViewHolder(View itemView) {
        super(itemView);

        status = (TextView) itemView.findViewById(R.id.status);
        name = (TextView) itemView.findViewById(R.id.name);
        price = (TextView) itemView.findViewById(R.id.price);
        loadurl = (Button) itemView.findViewById(R.id.loadurl);
    }

}
