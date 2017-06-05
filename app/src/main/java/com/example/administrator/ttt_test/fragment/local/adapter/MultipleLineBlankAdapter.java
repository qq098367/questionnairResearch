package com.example.administrator.ttt_test.fragment.local.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionOptionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/9.
 */

public class MultipleLineBlankAdapter extends RecyclerView.Adapter {
    private List<QuestionOptionVO> list=new ArrayList<>();
    private List<mViewHolder> holderList=new ArrayList<>();

    private class mViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private EditText et_blank;

        public mViewHolder(View itemView) {
            super(itemView);
            tv_title= (TextView) itemView.findViewById(R.id.textView_item_multi_line_blank);
            et_blank= (EditText) itemView.findViewById(R.id.editText_item_multi_line_blank);
        }
    }

    public MultipleLineBlankAdapter(List<QuestionOptionVO> list){
        this.list=list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_line_blank,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((mViewHolder)holder).tv_title.setText(list.get(position).getOption());
        holderList.add((mViewHolder)holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<String> getResult(){
        List<String> resultList=new ArrayList<>();
        int isEmpty=1;
        for (int i=0;i<list.size();i++){
            String data=holderList.get(i).et_blank.getText().toString();
            if(!data.equals("")){
                resultList.add(data);
                isEmpty*=1;
            }
            else {
                isEmpty*=0;
            }
        }
        if(isEmpty==0){
            return null;
        }
        else {
            return resultList;
        }
    }


    public void loadResult(List<String> tempList){
        for (int i=0;i<holderList.size();i++){
            holderList.get(i).et_blank.setText(tempList.get(i));
        }
    }
}
