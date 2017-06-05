package com.example.administrator.ttt_test.fragment.main;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.NoticeEntity;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;
import com.example.administrator.ttt_test.bean.SurveyorInfoVO;
import com.example.administrator.ttt_test.connection.MyHttpConnection;
import com.example.administrator.ttt_test.fragment.main.adapter.LastMissionAdapter;
import com.example.administrator.ttt_test.fragment.main.adapter.NoticeAdapter;
import com.example.administrator.ttt_test.fragment.main.adapter.SingleAdapter;
import com.example.administrator.ttt_test.util.file.MyFile;
import com.example.administrator.ttt_test.util.location.MyLocationFind;
import com.example.administrator.ttt_test.util.user.MyUser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_name;
    private TextView tv_unit;
    private TextView tv_location;
    private List<NoticeEntity> list=new ArrayList<>();
    private MyLocationFind myLocationFind;
    private String picAddress;
    private Bitmap userPic;
    private ImageView iv_photo;
    private RecyclerView rv_lastMission;
    private List<QuestionnaireSave> lastMissionList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contactsLayout = inflater.inflate(R.layout.fragment_mainpage, container, false);


//        initData();//传入假数据


//        QuestionnaireSave tempBean=new QuestionnaireSave();
//        tempBean.setPublishTime(new Date(1490946825000L));
//        tempBean.setQuestionnaireId(1L);
//        tempBean.setQuestionnaireTitle("第一份问卷");
//        tempBean.setFinishNum(0);
//        List<QuestionnaireSave> tempList=new ArrayList<>();
//        tempList.add(tempBean);
//        MyFile tempFile=new MyFile("fileList",MainFragment.this.getActivity());
//        tempFile.writeFile(JSON.toJSONString(tempList));
//        Toast.makeText(MainFragment.this.getActivity(),JSON.toJSONString(tempList),Toast.LENGTH_SHORT).show();

        recyclerView= (RecyclerView) contactsLayout.findViewById(R.id.recycleView_main_fragment);
        tv_location= (TextView) contactsLayout.findViewById(R.id.textView_mainfragment_location);
        tv_name= (TextView) contactsLayout.findViewById(R.id.textView_mainfragment_name);
        tv_unit= (TextView) contactsLayout.findViewById(R.id.textView_mainfragment_unit);
        iv_photo= (ImageView) contactsLayout.findViewById(R.id.imageView_fragment_mainpage_photo);
        rv_lastMission= (RecyclerView) contactsLayout.findViewById(R.id.recycleView_fragment_mainpage_lastMission);

        if(MyUser.isHavaLogin()){
            getNotice();
        }
        else {
            setNotLoginNotice();
            iv_photo.setImageResource(R.drawable.unknown_user);
        }
        getLocation();
//        loadLastMission();



        return contactsLayout;
    }

    private void setNotLoginNotice(){
        SingleAdapter adapter=new SingleAdapter();
        LinearLayoutManager manager=new LinearLayoutManager(MainFragment.this.getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }


    private void getNotice(){
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    JSONObject jsobj=new JSONObject(msg.obj.toString());
                    if(!jsobj.get("code").toString().equals("200")){
                        Toast.makeText(MainFragment.this.getActivity(),"获取公告失败",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        list=JSON.parseArray(jsobj.get("data").toString(), NoticeEntity.class);
                        NoticeAdapter adapter=new NoticeAdapter(list,MainFragment.this.getActivity());
                        LinearLayoutManager manager=new LinearLayoutManager(MainFragment.this.getActivity());
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        final Handler UserInfoHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONObject jsobj= null;
                try {
                    jsobj = new JSONObject(msg.obj.toString());
                    if(!jsobj.get("code").toString().equals("200")){
                        Toast.makeText(MainFragment.this.getActivity(),"获取个人信息失败",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SurveyorInfoVO tempEntity=new SurveyorInfoVO();
                        tempEntity=JSON.parseObject(jsobj.get("data").toString(), SurveyorInfoVO.class);
                        tv_name.setText("姓名："+tempEntity.getUserRealName());
                        tv_unit.setText("单位："+tempEntity.getUserUnit());
                        picAddress=tempEntity.getPicAddress();
//                        picAddress=picAddress.replaceAll(":","%3A");
//                        picAddress=picAddress.replaceAll("\\\\","%5C");
                        picAddress=picAddress.replaceAll(" ","%20");
                        downloadPic();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<NameValuePair> tempList=new ArrayList<NameValuePair>();
                MyHttpConnection myHttpConnection=new MyHttpConnection("/noticeRestful/listNoticeInfo",tempList,handler);
                myHttpConnection.startGetConnection();
                MyHttpConnection myHttpConnectionUserInfo=new MyHttpConnection("/userInfoRestful/getSurveyorInfo",tempList,UserInfoHandler);
                myHttpConnectionUserInfo.startGetConnection();

            }
        }.start();

    }


    private void getLocation(){
        checkPermission();
    }


    public void checkPermission(){
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainFragment.this.getActivity(),Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainFragment.this.getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainFragment.this.getActivity(),permissions,1);
        }

        else {
            Handler handler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if((myLocationFind.haveResult())){
                        String[] a = msg.obj.toString().split(",");
                        String lngIcon="°W";
                        String latIcon="°E";
                        tv_location.setText("纬度："+a[1]+"°W"+"   "+"经度："+a[0]+"°E");
                    }
                }
            };
            myLocationFind=new MyLocationFind(MainFragment.this.getActivity().getApplicationContext(),handler);
            myLocationFind.startService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myLocationFind.stopService();
    }

    @Override
    public void onResume() {
        super.onResume();


        MyFile lastMissionFile=new MyFile("lastMission",MainFragment.this.getActivity());
        List<QuestionnaireSave> tempList=new ArrayList<>();
        List<String> stringList=new ArrayList<>();
        String data=lastMissionFile.readFile();
        if(!data.isEmpty()){
            stringList=JSON.parseArray(data,String.class);
        }
        MyFile fileListFile=new MyFile("fileList",MainFragment.this.getActivity());
        String data1=fileListFile.readFile();
        if(!data1.isEmpty()){
            tempList=JSON.parseArray(data1,QuestionnaireSave.class);
        }

        lastMissionList=new ArrayList<>();
        for (int i=0;i<stringList.size();i++){
            for (int j=0;j<tempList.size();j++){
                if(tempList.get(j).getQuestionnaireId().toString().equals(stringList.get(i))){
                    lastMissionList.add(tempList.get(j));
                }
            }

        }
        LastMissionAdapter adapter=new LastMissionAdapter(lastMissionList,MainFragment.this.getActivity());
        LinearLayoutManager manager=new LinearLayoutManager(MainFragment.this.getActivity());
        rv_lastMission.setLayoutManager(manager);
        rv_lastMission.setAdapter(adapter);
    }

    public void downloadPic(){
        final MyFile myFile=new MyFile("picList",MainFragment.this.getActivity());
        JSONArray picArray= new JSONArray();
        boolean haveSame=false;
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.what==1) {
                    iv_photo.setImageBitmap(userPic);
                }

            }
        };
        try {
            String data=myFile.readFile();
            if(!data.isEmpty()) {
                picArray = new JSONArray(data);

                for (int i = 0; i < picArray.length(); i++) {
                    if (picArray.get(i).equals(picAddress)) {
                        haveSame = true;
                    }
                }
            }
            if(!haveSame){
                final JSONArray finalPicArray = picArray;
                new Thread(){
                    @Override
                    public void run() {
                        super.run();

//                        try {
//                            picAddress= URLEncoder.encode(picAddress,"UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }

                        MyHttpConnection myHttpConnection=new MyHttpConnection("/userPic/getUserPic?userPicAddr="+picAddress);
                        userPic=myHttpConnection.startGetPic();
                        if(userPic!=null){
                            try {
                                File picFile=new File(MainFragment.this.getActivity().getFilesDir().getAbsolutePath()+picAddress);
                                BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(picFile));
                                userPic.compress(Bitmap.CompressFormat.JPEG,80,out);
                                out.flush();
                                out.close();

                                finalPicArray.put(picAddress);
                                myFile.writeFile(finalPicArray.toString(),false);
                                loadPic(handler);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
            else {
                loadPic(handler);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadPic(Handler handler){
        try {
            File picFile=new File(MainFragment.this.getActivity().getFilesDir().getAbsolutePath()+picAddress);
            BufferedInputStream in=new BufferedInputStream(new FileInputStream(picFile));
            userPic= BitmapFactory.decodeStream(in);
            Message msg=new Message();
            msg.what=1;
            handler.sendMessage(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}