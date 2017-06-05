package com.example.administrator.ttt_test.fragment.main.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.activity.DoResearch;
import com.example.administrator.ttt_test.activity.ReadResult;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;
import com.example.administrator.ttt_test.util.file.MyFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/5/9.
 */

public class LastMissionAdapter extends RecyclerView.Adapter {
    private List<QuestionnaireSave> list=new ArrayList<>();
    private Context context;


    public LastMissionAdapter(List<QuestionnaireSave> list, Context context){
        this.list=list;
        this.context=context;
    }

    private class mViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_pic;
        private TextView tv_name;
        private TextView tv_finish;
        private View itemView;
        private LinearLayout line;

        public mViewHolder(View itemView) {
            super(itemView);
            iv_pic= (ImageView) itemView.findViewById(R.id.imageView_item_fragment_mainpage_last_mission);
            tv_name= (TextView) itemView.findViewById(R.id.textView_item_fragment_mainpage_last_mission_name);
            tv_finish= (TextView) itemView.findViewById(R.id.textView_item_fragment_mainpage_last_mission_finish);
            this.itemView=itemView;
            line= (LinearLayout) itemView.findViewById(R.id.line_item_fragment_mainpage_last_mission_finish);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_fragment_mainpage_last_mission,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        mViewHolder myViewHolder= (mViewHolder) holder;
        String title=list.get(position).getQuestionnaireTitle();

        if(title.length()>=14){
            title=title.substring(0,12)+"...";
        }
        myViewHolder.tv_name.setText(title);
        myViewHolder.tv_finish.setText("已完成："+list.get(position).getFinishNum()+"份");
        if(position==1){
            myViewHolder.line.setBackgroundColor(Color.WHITE);
        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout=LayoutInflater.from(context).inflate(R.layout.dialog_local_fragment_do_or_read,null);
                Button bt_dialogDo= (Button) layout.findViewById(R.id.button_dialog_doOrRead_do);
                Button bt_dialogRead= (Button) layout.findViewById(R.id.button_dialog_doOrRead_read);
                final AlertDialog dialog=new AlertDialog.Builder(context).setView(layout).show();

                bt_dialogDo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if(list.get(position).isUnfinishResult()){
                            Handler handler=new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    Intent intent=new Intent(context, DoResearch.class);
                                    if(msg.what==0){
                                        intent.putExtra("haveUnfinishResult","false");
                                    }
                                    else {
                                        intent.putExtra("haveUnfinishResult","true");
                                    }
                                    intent.putExtra("fileId",list.get(position).getQuestionnaireId().toString());
                                    intent.putExtra("researchId",list.get(position).getResearchId().toString());
                                    context.startActivity(intent);
                                }
                            };
                            unFinishResultCheckDialog(context,handler);
                        }
                        else {
                            Intent intent=new Intent(context, DoResearch.class);
                            intent.putExtra("haveUnfinishResult","false");
                            intent.putExtra("fileId",list.get(position).getQuestionnaireId().toString());
                            intent.putExtra("researchId",list.get(position).getResearchId().toString());
                            context.startActivity(intent);
                        }
                    }
                });

                bt_dialogRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MyFile myFile=new MyFile(list.get(position).getQuestionnaireId().toString()+"NotUpload",context);
                        String data=myFile.readFile();
                        if(!data.isEmpty()){
                            Intent intent=new Intent(context, ReadResult.class);
                            intent.putExtra("fileId",list.get(position).getQuestionnaireId().toString());
                            intent.putExtra("researchId",list.get(position).getResearchId().toString());
                            context.startActivity(intent);
                        }
                        else {
                            Toast.makeText(context,"当前没有未上传结果可供浏览",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void unFinishResultCheckDialog(final Context context, final Handler handler){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("是否继续上次未完成任务？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Message msg=new Message();
                msg.what=1;
                handler.sendMessage(msg);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Message msg=new Message();
                msg.what=0;
                handler.sendMessage(msg);
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
