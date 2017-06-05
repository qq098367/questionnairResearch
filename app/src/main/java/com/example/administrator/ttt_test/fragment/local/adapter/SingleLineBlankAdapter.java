package com.example.administrator.ttt_test.fragment.local.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionOptionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/9.
 */

public class SingleLineBlankAdapter extends RecyclerView.Adapter {
    private List<QuestionOptionVO> list=new ArrayList<>();
    private Context context;
    private String result;
    private mViewHolder blankHolder;

    private class mViewHolder extends RecyclerView.ViewHolder {
        private EditText et_blank;

        public mViewHolder(View itemView) {
            super(itemView);
            et_blank= (EditText) itemView.findViewById(R.id.editText_item_single_line_blank);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_line_blank,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        blankHolder=((mViewHolder)holder);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public List<String> getResult(){
        List<String> resultList=new ArrayList<>();
        String data=blankHolder.et_blank.getText().toString();
        if(data.equals("")){
            return null;
        }
        else {
            resultList.add(data);
            return resultList;
        }
    }

    public void loadResult(List<String> tempList){
        blankHolder.et_blank.setText(tempList.get(0));
    }
}
