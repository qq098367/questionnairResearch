package com.example.administrator.ttt_test.util.file;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Acer on 2017/4/22.
 */

public class MyFile {
    private FileInputStream in=null;
    private BufferedReader reader=null;
    private FileOutputStream out=null;
    private BufferedWriter writer=null;
    private Context context;
    private String fileName;
    private List<QuestionnaireSave> fileList=new ArrayList<>();
    private Long researchId;
    private Date publishDate;
    private Long questionId;


    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Long getResearchId() {
        return researchId;
    }

    public void setResearchId(Long researchId) {
        this.researchId = researchId;
    }

    public MyFile(String fileName, Context context){
        this.fileName=fileName;
        this.context=context;
    }

    public MyFile(Context context){
        this.context=context;
    }


    public String readFile(){
        StringBuilder content=new StringBuilder();
        try {
            in=context.openFileInput(fileName);
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while ((line=reader.readLine())!=null){
                content.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    public boolean writeFile(String data,boolean addToFileList){
        try {
            out=context.openFileOutput(fileName,Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                    //加入本地文件表
                    if(addToFileList) {
                        addToFileList(data);

                    }

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public List<QuestionnaireSave> getFileList(){
        String temp=null;
        fileName="fileList";
        temp=readFile();
        if(!temp.isEmpty()){
            fileList= JSON.parseArray(temp,QuestionnaireSave.class);
        }
        return fileList;
    }

    public void addToFileList(String data){
        getFileList();
        FileOutputStream tempOut=null;
        BufferedWriter tempWriter=null;
        try {
            JSONObject jsonStart=new JSONObject(data);
            String title= jsonStart.get("questionnaireTitle").toString();
            String id=jsonStart.get("questionnaireId").toString();
            String researchId=getResearchId()+"";


            boolean need=true;
            for (int i=0;i<fileList.size();i++){
                if(fileList.get(i).getQuestionnaireId()==Long.parseLong(id)){
                    need=false;
                    break;
                }
            }
            if(need) {
                QuestionnaireSave tempBean=new QuestionnaireSave();
                tempBean.setQuestionnaireId(Long.parseLong(id));
                tempBean.setQuestionnaireTitle(title);
                tempBean.setResearchId(new Long(researchId));
                tempBean.setPublishTime(getPublishDate());
                tempBean.setUnfinishResult(false);
                fileList.add(tempBean);
                JSONArray jsonFinish = new JSONArray(JSON.toJSONString(fileList));
                tempOut = context.openFileOutput("fileList", Context.MODE_PRIVATE);
                tempWriter = new BufferedWriter(new OutputStreamWriter(tempOut));
                tempWriter.write(jsonFinish.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(tempWriter!=null){
                try {
                    tempWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void deleteFile(String name, Activity activity){
        File file=new File(activity.getFilesDir()+"/"+name);
        if(file.isFile()&&file.exists()){
            file.delete();
        }
    }



}
