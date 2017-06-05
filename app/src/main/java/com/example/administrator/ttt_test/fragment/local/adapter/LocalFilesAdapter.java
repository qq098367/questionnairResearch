package com.example.administrator.ttt_test.fragment.local.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.activity.DoResearch;
import com.example.administrator.ttt_test.activity.ReadResult;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;
import com.example.administrator.ttt_test.util.date.MyDateTransform;
import com.example.administrator.ttt_test.util.file.MyFile;

import java.util.List;

/**
 * Created by Acer on 2017/4/7.
 */

public class LocalFilesAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<QuestionnaireSave> dataList;

    public class mViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private View itemView;
        private TextView tv_finishNum;
        private TextView tv_publishTime;


        public mViewHolder(View itemView) {
            super(itemView);

            tv_title= (TextView) itemView.findViewById(R.id.textView_item_each_local_file_title);
            tv_finishNum= (TextView) itemView.findViewById(R.id.textView_item_each_local_file_progress);
            tv_publishTime= (TextView) itemView.findViewById(R.id.textView_item_each_local_file_time);
            this.itemView=itemView;
        }
    }

    public LocalFilesAdapter(Context context,List<QuestionnaireSave> dataList){
        this.context=context;
        this.dataList=dataList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_each_local_file,parent,false);
        mViewHolder holder=new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String title=dataList.get(position).getQuestionnaireTitle();
//        if(title.length()>12){
//            ((mViewHolder)holder).tv_title.setTextSize(16);
//        }
        if(title.length()>23){
            title=title.substring(0,21)+"...";
        }
        ((mViewHolder)holder).tv_title.setText(title);
        ((mViewHolder)holder).tv_publishTime.setText(MyDateTransform.getDateFromLong(dataList.get(position).getPublishTime().getTime()));
        ((mViewHolder)holder).tv_finishNum.setText(dataList.get(position).getFinishNum()+"");
        ((mViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout=LayoutInflater.from(context).inflate(R.layout.dialog_local_fragment_do_or_read,null);
                Button bt_dialogDo= (Button) layout.findViewById(R.id.button_dialog_doOrRead_do);
                Button bt_dialogRead= (Button) layout.findViewById(R.id.button_dialog_doOrRead_read);
                final AlertDialog dialog=new AlertDialog.Builder(context).setView(layout).show();
                bt_dialogDo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //做问卷
                        dialog.dismiss();
                        if(dataList.get(position).isUnfinishResult()){
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
                                    intent.putExtra("fileId",dataList.get(position).getQuestionnaireId().toString());
                                    intent.putExtra("researchId",dataList.get(position).getResearchId().toString());
                                    context.startActivity(intent);
                                }
                            };
                            unFinishResultCheckDialog(context,handler);
                        }
                        else {
                            Intent intent=new Intent(context, DoResearch.class);
                            intent.putExtra("haveUnfinishResult","false");
                            intent.putExtra("fileId",dataList.get(position).getQuestionnaireId().toString());
                            intent.putExtra("researchId",dataList.get(position).getResearchId().toString());
                            context.startActivity(intent);
                        }
                    }
                });
                bt_dialogRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //浏览结果
                        MyFile myFile=new MyFile(dataList.get(position).getQuestionnaireId().toString()+"NotUpload",context);
                        String data=myFile.readFile();
                        if(!data.isEmpty()){
                            Intent intent=new Intent(context, ReadResult.class);
                            intent.putExtra("fileId",dataList.get(position).getQuestionnaireId().toString());
                            intent.putExtra("researchId",dataList.get(position).getResearchId().toString());
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
        return dataList.size();
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
