package com.example.administrator.ttt_test.fragment.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.NoticeEntity;
import com.example.administrator.ttt_test.util.date.MyDateTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/6.
 */

public class NoticeAdapter extends RecyclerView.Adapter {
    private List<NoticeEntity> list=new ArrayList<>();
    private Context context;

    public NoticeAdapter(List<NoticeEntity> list, Context context){
        this.list=list;
        this.context=context;
    }


    public class mViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_content;
        private TextView tv_time;
        private View itemView;

        public mViewHolder(View itemView) {
            super(itemView);
            tv_content= (TextView) itemView.findViewById(R.id.textView_item_mainfragment_notice_content);
            tv_time= (TextView) itemView.findViewById(R.id.textView_item_mainfragment_notice_time);
            this.itemView=itemView;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainfragment_notice,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String title=list.get(position).getNoticeTitle();
        if(title.length()>=14){
            title=title.substring(0,12)+"...";
        }
        ((mViewHolder)holder).tv_content.setText(title);
        ((mViewHolder)holder).tv_time.setText(MyDateTransform.getDateFromLong(list.get(position).getNoticeLaunchDate().getTime()));
        ((mViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void createDialog(NoticeEntity notice){
        View view=LayoutInflater.from(context).inflate(R.layout.dialog_notice,null);
        TextView tv_dialogTitle= (TextView) view.findViewById(R.id.textView_dialog_notice_title);
        TextView tv_dialogContent= (TextView) view.findViewById(R.id.textView_dialog_notice_content);
        TextView tv_dialogCreateUnit= (TextView) view.findViewById(R.id.textView_dialog_notice_createUnit);
        TextView tv_dialogTime= (TextView) view.findViewById(R.id.textView_dialog_notice_time);
        tv_dialogTitle.setText(notice.getNoticeTitle());
        tv_dialogContent.setText(notice.getNoticeContent());
        tv_dialogCreateUnit.setText(notice.getCreateUnit());
        tv_dialogTime.setText(MyDateTransform.getDateFromLong(notice.getNoticeLaunchDate().getTime()));
        AlertDialog dialog=new  AlertDialog.Builder(context).setView(view).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);


    }
}
