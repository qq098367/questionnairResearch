package com.example.administrator.ttt_test.fragment.local.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionOptionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/9.
 */

public class SingleChoiceAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<QuestionOptionVO> list=new ArrayList<>();
    private mViewHolder checkedHolder=null;
    private List<mViewHolder> holders=new ArrayList<>();

    public SingleChoiceAdapter(List<QuestionOptionVO> list,Context context){
        this.list=list;
        this.context=context;
    }

    private class mViewHolder extends RecyclerView.ViewHolder {
        private RadioButton rb_option;
        private View itemView;

        public mViewHolder(View itemView) {
            super(itemView);
            rb_option= (RadioButton) itemView.findViewById(R.id.radioButton_item_single_choice);
            this.itemView=itemView;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_choice,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((mViewHolder)holder).rb_option.setText(list.get(position).getOption());
        ((mViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedHolder!=null) {
                    checkedHolder.rb_option.setChecked(false);
                }
                checkedHolder=((mViewHolder)holder);
                checkedHolder.rb_option.setChecked(true);
            }
        });
        holders.add((mViewHolder)holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<String> getResult(){
        List<String> resultList=new ArrayList<>();
        if(checkedHolder!=null){
            resultList.add(checkedHolder.rb_option.getText().toString());
            return resultList;
        }
        else {
            return null;
        }
    }

    public void loadResult(List<String> tempList){
        for (int i=0;i<holders.size();i++){
            if(tempList.get(0).equals(holders.get(i).rb_option.getText())){
                holders.get(i).rb_option.setChecked(true);
                checkedHolder=holders.get(i);
                break;
            }
        }
    }

}
