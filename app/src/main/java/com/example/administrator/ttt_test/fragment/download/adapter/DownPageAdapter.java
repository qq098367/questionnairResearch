package com.example.administrator.ttt_test.fragment.download.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;
import com.example.administrator.ttt_test.connection.MyHttpConnection;
import com.example.administrator.ttt_test.util.date.MyDateTransform;
import com.example.administrator.ttt_test.util.file.MyFile;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2017/4/20.
 */

public class DownPageAdapter extends RecyclerView.Adapter {
    private List<QuestionnaireSave> dataList=new ArrayList<>();
    private Context context;
    private static int HAVA_DOWNLOADED=1;
    private static int NOT_DOWNLOADED=0;
    private int type;


    public DownPageAdapter(List<QuestionnaireSave> dataList, Context context,int type){
        this.dataList=dataList;
        this.context=context;
        this.type=type;
    }


    private class mViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_fileName;
        private TextView tv_publishTime;
        private ImageView iv_fileImage;

        public mViewHolder(View itemView) {
            super(itemView);
            tv_fileName= (TextView) itemView.findViewById(R.id.textView_item_download_fragment_fileName);
            iv_fileImage= (ImageView) itemView.findViewById(R.id.imageView_item_download_fragment_downloadImage);
            tv_publishTime= (TextView) itemView.findViewById(R.id.textView_item_download_fragment_publishDate);
        }
    }

    private class downloadedViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_filename;
        public downloadedViewHolder(View itemView) {
            super(itemView);
            tv_filename= (TextView) itemView.findViewById(R.id.textView_item_download_fragment_downloaded_fileName);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(type==NOT_DOWNLOADED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_fragment, parent, false);
            mViewHolder holder = new mViewHolder(view);
            return holder;
        }
        else {
            View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_download_downloaded,parent,false);
            downloadedViewHolder downloadedViewHolder=new downloadedViewHolder(view);
            return downloadedViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(type==NOT_DOWNLOADED){
            String fileName=dataList.get(position).getQuestionnaireTitle().toString();

            if(fileName.length()>=14){
                fileName=fileName.substring(0,12)+"...";
            }
            ((mViewHolder)holder).tv_fileName.setText(fileName);
            ((mViewHolder)holder).iv_fileImage.setImageResource(R.drawable.download_file);
            ((mViewHolder)holder).tv_publishTime.setText(MyDateTransform.getDateFromLong(dataList.get(position).getPublishTime().getTime()));
            //下载按钮
            ((mViewHolder)holder).iv_fileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Handler handler=new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.obj.toString().isEmpty()){
                                Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                MyFile myFile=new MyFile(dataList.get(position).getQuestionnaireId()+"",context);
                                myFile.setResearchId(dataList.get(position).getResearchId());
                                myFile.setPublishDate(dataList.get(position).getPublishTime());
                                myFile.writeFile(msg.obj.toString(),true);
                                ((mViewHolder)holder).iv_fileImage.setImageResource(R.drawable.download_finish);

                            }
                        }
                    };
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            List<NameValuePair> tempList=new ArrayList<NameValuePair>();
                            MyHttpConnection httpConnection=new MyHttpConnection("/QesManageRest/displayQuestionnaire/"+dataList.get(position).getQuestionnaireId(),tempList,handler);
                            httpConnection.startGetConnection();
                        }
                    }.start();
                }
            });
        }
        else{
            String data=dataList.get(position).getQuestionnaireTitle();
            if(data.length()>22){
                data=data.substring(0,21)+"...";
            }
            ((downloadedViewHolder)holder).tv_filename.setText(data);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
