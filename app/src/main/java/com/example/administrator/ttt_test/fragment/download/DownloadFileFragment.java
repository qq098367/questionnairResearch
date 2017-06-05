package com.example.administrator.ttt_test.fragment.download;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;
import com.example.administrator.ttt_test.bean.temp.ResearchListVO;
import com.example.administrator.ttt_test.bean.temp.ResearchQesPaperVO;
import com.example.administrator.ttt_test.connection.MyHttpConnection;
import com.example.administrator.ttt_test.fragment.download.adapter.DownPageAdapter;
import com.example.administrator.ttt_test.util.file.MyFile;
import com.example.administrator.ttt_test.util.user.MyUser;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class DownloadFileFragment extends Fragment {
    private Button bt_haveDownload;
    private Button bt_notDownload;
    private RecyclerView rv_download;
    private List<QuestionnaireSave> havaDownloadList=new ArrayList<>();
    private List<QuestionnaireSave> notDownloadList=new ArrayList<>();
    private List<QuestionnaireSave> afterDealList;
    private List<ResearchListVO> notDealList=new ArrayList<>();

    private int[] buttonCheck=new int[2];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contactsLayout = inflater.inflate(R.layout.fragment_download,
                container, false);

        bt_haveDownload= (Button) contactsLayout.findViewById(R.id.button_fragment_download_haveDownload);
        bt_notDownload= (Button) contactsLayout.findViewById(R.id.button_fragment_download_notDownload);
        rv_download= (RecyclerView) contactsLayout.findViewById(R.id.recycleView_fragment_download);

        //初始化页面相关数据
        buttonCheck[0]=1;
        buttonCheck[1]=0;
        initData();



        //未下载按钮点击事件
        bt_notDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonCheck[0]==0){
                    //界面操作
                    buttonCheck[0]=1;
                    buttonCheck[1]=0;
                    bt_notDownload.setBackgroundResource(R.drawable.rb_bg_01t);
                    bt_haveDownload.setBackgroundResource(R.drawable.rb_bg_02f);
                    initData();
                }
            }
        });

        //已下载按钮点击事件
        bt_haveDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonCheck[1]==0){
                    //界面操作
                    buttonCheck[0]=0;
                    buttonCheck[1]=1;
                    bt_notDownload.setBackgroundResource(R.drawable.rb_bg_01f);
                    bt_haveDownload.setBackgroundResource(R.drawable.rb_bg_02t);
                    initData();

                }
            }
        });

        return contactsLayout;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        initData();

    }

    public void setHaveDownloadAdapter(){
        Context context=DownloadFileFragment.this.getActivity();
        LinearLayoutManager manager=new LinearLayoutManager(context);
        DownPageAdapter adapter=new DownPageAdapter(havaDownloadList,context,1);
        rv_download.setLayoutManager(manager);
        rv_download.setAdapter(adapter);
    }

    public void setNotDownloadAdapter(){
        Context context=DownloadFileFragment.this.getActivity();
        LinearLayoutManager manager=new LinearLayoutManager(context);
        DownPageAdapter adapter=new DownPageAdapter(notDownloadList,context,0);
        rv_download.setLayoutManager(manager);
        rv_download.setAdapter(adapter);
    }

    public void initData(){
        if(MyUser.isHavaLogin()) {
            getNotDownloadList();
        }
        else {
            Toast.makeText(DownloadFileFragment.this.getActivity(),"未登录状态无法显示本页面",Toast.LENGTH_SHORT).show();
        }
    }

    public void getHavaDownloadList() {
        MyFile myFile=new MyFile(DownloadFileFragment.this.getActivity());
        havaDownloadList=myFile.getFileList();
    }

    public void getNotDownloadList() {
        //测试用添加文件
//        String data="{\"questionnaireId\":1,\"questionnaireTitle\":\"第一份问卷\",\"questionnaireSubtitle\":\"问卷副标题\",\"questionnaireDescription\":\"问卷描述\",\"questions\":[{\"questionId\":1,\"questionContext\":\"性别\",\"questionType\":\"单选题\",\"questionDescription\":\"0\",\"options\":[{\"optionOrder\":0,\"option\":\"男\"},{\"optionOrder\":1,\"option\":\"女\"}],\"must\":true},{\"questionId\":2,\"questionContext\":\"兴趣爱好\",\"questionType\":\"多选题\",\"questionDescription\":\"0\",\"options\":[{\"optionOrder\":0,\"option\":\"篮球\"},{\"optionOrder\":1,\"option\":\"羽毛球\"},{\"optionOrder\":2,\"option\":\"足球\"},{\"optionOrder\":3,\"option\":\"橄榄球\"}],\"must\":false}]}";
//        DisplayQuestionnaireVO tempBean=JSON.parseObject(data, DisplayQuestionnaireVO.class);
//        MyFile myFile=new MyFile(tempBean.getQuestionnaireId().toString(),DownloadFileFragment.this.getActivity());
//        myFile.writeFile(data);

        /* 1.通过http获得List<ResearchListVO>
         * 2.List<ResearchListVO>解开成List<ResearchQesPaper>(顺便整成需要的格式)
         * 3.从fileList中读出已经存在的问卷
         * 4.两者对比，构成“未下载列表”和“已下载列表”
         *
         *
         */

        //一些初始化工作
        afterDealList=new ArrayList<>();
        notDownloadList=new ArrayList<>();

        //1.通过http获得List<ResearchListVO>
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.obj.toString().isEmpty()){
                    notDownloadList=new ArrayList<>();
                }
                else {
                    notDealList = JSON.parseArray(msg.obj.toString(), ResearchListVO.class);
                }
                //2.List<ResearchListVO>解开成List<ResearchQesPaper>（顺便整成需要的格式List<QuestionnaireSava>）
                dealHttpList(notDealList);

                //3.从fileList中读出已经存在的问卷
                MyFile tempMyFile=new MyFile(DownloadFileFragment.this.getActivity());
                getHavaDownloadList();

                //4.两者对比，构成“未下载列表”和“已下载列表”
                for (int i=0;i<afterDealList.size();i++){
                    boolean same=false;
                    for (int j=0;j<havaDownloadList.size();j++){
                        if(afterDealList.get(i).getQuestionnaireId()==havaDownloadList.get(j).getQuestionnaireId()){
                            same=true;
                            break;
                        }
                    }
                    if(!same){
                        notDownloadList.add(afterDealList.get(i));
                    }

                }
                if(buttonCheck[0]==1){
                    setNotDownloadAdapter();
                }
                else {
                    setHaveDownloadAdapter();
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<NameValuePair> tempList=new ArrayList<NameValuePair>();
                MyHttpConnection myConnection=new MyHttpConnection("/researchManage/listResearchMission",tempList,handler);
                myConnection.startGetConnection();
            }
        }.start();

    }

    public void dealHttpList(List<ResearchListVO> notDealList){
        for (int i=0;i<notDealList.size();i++){
            List<ResearchQesPaperVO> tempList=notDealList.get(i).getResearchQesPaperVOList();
            for (int j=0;j<tempList.size();j++){
                QuestionnaireSave tempBean=new QuestionnaireSave();
                tempBean.setQuestionnaireTitle(tempList.get(j).getQuestionnaireTitle());
                tempBean.setQuestionnaireId(tempList.get(j).getQuestionnaireId());
                tempBean.setPublishTime(notDealList.get(i).getResearchLaunchDate());
                tempBean.setResearchId(notDealList.get(i).getResearchId());
                afterDealList.add(tempBean);
            }
        }

    }
}