package com.example.administrator.ttt_test.fragment.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.ttt_test.R;

/**
 * Created by Acer on 2017/5/26.
 */

public class SingleAdapter extends RecyclerView.Adapter {


    private class mViewHolder extends RecyclerView.ViewHolder {
        public mViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_not_connect_notice,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
