package com.example.administrator.ttt_test.fragment.local.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionOptionVO;
import com.example.administrator.ttt_test.util.path.ImagePath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/9.
 */

public class PictureSelectAdapter extends RecyclerView.Adapter {
    private List<QuestionOptionVO> list=new ArrayList<>();
    private Context context;
    private long questionnaireId;

    private class mViewHolder extends RecyclerView.ViewHolder {
        private RadioButton rb_choice;
        private ImageView iv_choice;

        public mViewHolder(View itemView) {
            super(itemView);
            rb_choice= (RadioButton) itemView.findViewById(R.id.radioButton_item_single_image_choice);
            iv_choice= (ImageView) itemView.findViewById(R.id.imageView_item_single_image_choice);
        }
    }

    public PictureSelectAdapter(List<QuestionOptionVO> list,Context context,long questionnaireId){
        this.list=list;
        this.context=context;
        this.questionnaireId=questionnaireId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_image_choice,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file=new File(ImagePath.getImagePath()+questionnaireId+"/"+list.get(position).getOption());
        if(file.exists()){
            Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
            ((mViewHolder)holder).iv_choice.setImageBitmap(bitmap);
        }
        else {
            Toast.makeText(context,"图片不存在",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
