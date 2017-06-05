package com.example.administrator.ttt_test.fragment.local;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.AnswerPaperVO;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;
import com.example.administrator.ttt_test.connection.MyHttpConnection;
import com.example.administrator.ttt_test.fragment.local.adapter.LocalFilesAdapter;
import com.example.administrator.ttt_test.util.file.MyFile;
import com.example.administrator.ttt_test.util.user.MyUser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalFileFragment extends Fragment {
    private RecyclerView rv_files;
    private ImageView iv_upload;
    private List<QuestionnaireSave> dataList=new ArrayList<>();
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_local_file, container, false);


        rv_files= (RecyclerView) view.findViewById(R.id.recycleView_fragment_local_file);
        iv_upload= (ImageView) view.findViewById(R.id.imageView_fragment_local_file_upload);

        iv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyUser.isHavaLogin()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LocalFileFragment.this.getActivity());
                    builder.setTitle("批量上传");
                    builder.setMessage("是否批量上传本地数据？");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);

                                    if(msg.obj.toString().equals("empty")){
                                        Toast.makeText(LocalFileFragment.this.getActivity(), "暂时没有未上传数据", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        try {
                                            JSONObject tempObject = new JSONObject(msg.obj.toString());
                                            if (tempObject.get("code").toString().equals("200")) {
                                                MyFile myFile = new MyFile("notUploadFileList", LocalFileFragment.this.getActivity());
                                                try {
                                                    List<AnswerPaperVO> list = new ArrayList<>();
                                                    String tempData = null;
                                                    tempData = myFile.readFile();
                                                    if (!tempData.isEmpty()) {
                                                        JSONArray jsonArray = new JSONArray(tempData);
                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                            MyFile deleteNotUploadFile = new MyFile(LocalFileFragment.this.getActivity());
                                                            deleteNotUploadFile.deleteFile(jsonArray.get(i).toString() + "NotUpload", LocalFileFragment.this.getActivity());
                                                        }
                                                        myFile.deleteFile("notUploadFileList", LocalFileFragment.this.getActivity());
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Toast.makeText(LocalFileFragment.this.getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LocalFileFragment.this.getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            };

                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    String data = readNotUploadList();
                                    if (!data.isEmpty()) {
                                        List<NameValuePair> list = new ArrayList<NameValuePair>();
                                        list.add(new BasicNameValuePair("answerPaperVOList", data));
                                        MyHttpConnection myHttpConnection = new MyHttpConnection("/researchResult/submitAnswerPaper", data, handler);
                                        myHttpConnection.startPostConnectionNeedHead();
                                    }
                                    else {
                                        Message msg=new Message();
                                        msg.obj="empty";
                                        handler.sendMessage(msg);
                                    }
                                }
                            }.start();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Toast.makeText(LocalFileFragment.this.getActivity(), "请登录后再使用此功能", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }

    public void initData(){
        MyFile tempMyFile=new MyFile(LocalFileFragment.this.getActivity());
        dataList=tempMyFile.getFileList();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();

        LocalFilesAdapter adapter=new LocalFilesAdapter(this.getActivity(),dataList);
        GridLayoutManager manager=new GridLayoutManager(this.getActivity(),2);
        rv_files.setLayoutManager(manager);
        rv_files.setAdapter(adapter);

    }

    private String readNotUploadList(){
        String data="";
        MyFile myFile=new MyFile("notUploadFileList",LocalFileFragment.this.getActivity());
        try {
            List<AnswerPaperVO> list=new ArrayList<>();
            String tempData=null;
            tempData=myFile.readFile();
            if(!tempData.isEmpty()) {
                JSONArray jsonArray = new JSONArray(tempData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    MyFile readNotUploadFile = new MyFile(jsonArray.get(i).toString() + "NotUpload", LocalFileFragment.this.getActivity());
                    JSONArray tempArray = new JSONArray(readNotUploadFile.readFile());
                    List<AnswerPaperVO> tempList = JSON.parseArray(readNotUploadFile.readFile(), AnswerPaperVO.class);
                    list.addAll(tempList);
                }
                data=JSON.toJSONString(list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        initData();

        LocalFilesAdapter adapter=new LocalFilesAdapter(this.getActivity(),dataList);
        GridLayoutManager manager=new GridLayoutManager(this.getActivity(),2);
        rv_files.setLayoutManager(manager);
        rv_files.setAdapter(adapter);

    }
}