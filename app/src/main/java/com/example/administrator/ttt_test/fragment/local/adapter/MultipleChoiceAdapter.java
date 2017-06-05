package com.example.administrator.ttt_test.fragment.local.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionOptionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/9.
 */

public class MultipleChoiceAdapter extends RecyclerView.Adapter {
    private List<QuestionOptionVO> list=new ArrayList<>();
    private Context context;
    private List<String> dataList=new ArrayList<>();
    private List<mViewHolder> holderList=new ArrayList<>();

    public MultipleChoiceAdapter(List<QuestionOptionVO> list,Context context){
        this.list=list;
        this.context=context;
    }

    private class mViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb_choice;
        private View itemView;

        public mViewHolder(View itemView) {
            super(itemView);
            cb_choice= (CheckBox) itemView.findViewById(R.id.checkBox_item_multiple_choice);
            this.itemView=itemView;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multiple_choice,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((mViewHolder)holder).cb_choice.setText(list.get(position).getOption());
        holderList.add(((mViewHolder)holder));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<String> getResult(){
        for (int i=0;i<holderList.size();i++){
            if(holderList.get(i).cb_choice.isChecked()){
                dataList.add(holderList.get(i).cb_choice.getText().toString());
            }
        }
        if(dataList.size()!=0){
            return dataList;
        }
        else {
            return null;
        }

    }


    public void loadResult(List<String> tempList){
        for (int i=0;i<holderList.size();i++){
            for (int j=0;j<tempList.size();j++){
                if(holderList.get(i).cb_choice.getText().equals(tempList.get(j))){
                    holderList.get(i).cb_choice.setChecked(true);
                    break;
                }
            }
        }
    }
}
