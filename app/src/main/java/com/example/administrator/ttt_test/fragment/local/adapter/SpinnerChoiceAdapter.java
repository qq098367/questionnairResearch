package com.example.administrator.ttt_test.fragment.local.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionOptionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/9.
 */

public class SpinnerChoiceAdapter extends RecyclerView.Adapter {
    private List<QuestionOptionVO> list=new ArrayList<>();
    private Context context;
    private List<String> dataList=new ArrayList<>();
    private String result;
    private List<mViewHolder> holders=new ArrayList<>();

    public class mViewHolder extends RecyclerView.ViewHolder {
        private Spinner spinner;
        private View itemView;

        public mViewHolder(View itemView) {
            super(itemView);
            spinner= (Spinner) itemView.findViewById(R.id.spinner_item_spinner_choice);
            this.itemView=itemView;

            //处理选项数据
            for (int i=0;i<list.size();i++){
                dataList.add(list.get(i).getOption());
            }

        }
    }


    public SpinnerChoiceAdapter(List<QuestionOptionVO> list ,Context context){
        this.list=list;
        this.context=context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_choice,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ArrayAdapter adapter=new ArrayAdapter(context,R.layout.item_of_spinner_choice,R.id.textView_item_of_spinner_choice,dataList);
        ((mViewHolder)holder).spinner.setAdapter(adapter);
        ((mViewHolder)holder).spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv_result= (TextView) view.findViewById(R.id.textView_item_of_spinner_choice);
                result=tv_result.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holders.add((mViewHolder)holder);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public List<String> getResult(){
        List<String> resultList=new ArrayList<>();
        resultList.add(result);
        return resultList;
    }

    public void loadResult(List<String> tempList){
        for (int i=0;i<holders.size();i++){
            for (int j=0;j<dataList.size();j++){
                if(dataList.get(j).equals(tempList.get(i))){
                    holders.get(0).spinner.setSelection(j);
                    result=tempList.get(i);
                }
            }
        }
    }
}
