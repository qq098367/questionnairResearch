package com.example.administrator.ttt_test.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.bean.AnswerDetailVO;
import com.example.administrator.ttt_test.bean.AnswerPaperVO;
import com.example.administrator.ttt_test.bean.DisplayQuestionnaireVO;
import com.example.administrator.ttt_test.bean.QuestionOptionVO;
import com.example.administrator.ttt_test.bean.QuestionVO;
import com.example.administrator.ttt_test.bean.QuestionnaireSave;
import com.example.administrator.ttt_test.bean.Result;
import com.example.administrator.ttt_test.bean.TempResultSave;
import com.example.administrator.ttt_test.broadcast.NetworkChangeReceiver;
import com.example.administrator.ttt_test.fragment.local.adapter.MultipleChoiceAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.MultipleLineBlankAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.PictureSelectAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.SingleChoiceAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.SingleLineBlankAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.SpinnerChoiceAdapter;
import com.example.administrator.ttt_test.util.file.MyFile;
import com.example.administrator.ttt_test.util.location.MyLocationFind;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseArray;

public class DoResearch extends AppCompatActivity {
    private DisplayQuestionnaireVO questionnaire=new DisplayQuestionnaireVO();
    private List<QuestionVO> questions=new ArrayList<>();
    private List<QuestionOptionVO> options=new ArrayList<>();
    private List<RecyclerView> recycleViewList=new ArrayList<>();
    private int questionNum=0;
    private Result result=new Result();
    private AnswerPaperVO answerPaper=new AnswerPaperVO();
    private List<AnswerDetailVO> resultList=new ArrayList<>();
    private MyLocationFind locationFind;
    private NetworkChangeReceiver receiver;
    private IntentFilter intentFilter;
    private Long researchIdFromMain;
    private String haveUnfinishResult;
    private boolean haveShow=false;



    private TextView tv_title;
    private TextView tv_subtitle;
    private TextView tv_description;
    private TextView  tv_back;
    private LinearLayout layout_questions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_research);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        //放入测试用数据
//        inputData();
        String data=getIntent().getStringExtra("researchId");
        researchIdFromMain=new Long(getIntent().getStringExtra("researchId"));
        loadFile(getIntent().getStringExtra("fileId"));
        haveUnfinishResult=getIntent().getStringExtra("haveUnfinishResult");

        //加载网络确认
        setInfilter();
        checkPermission();




        tv_title= (TextView) this.findViewById(R.id.textView_do_research_title);
        tv_subtitle= (TextView) this.findViewById(R.id.textView_do_research_subtitle);
        tv_description= (TextView) this.findViewById(R.id.textView_do_research_description);
        tv_back= (TextView) this.findViewById(R.id.textView_do_research_back);
        layout_questions= (LinearLayout) this.findViewById(R.id.layout_questions);


        //初始化基本数据
        if(questionnaire.getQuestionnaireTitle().length()>10){
            tv_title.setTextSize(21);
        }
        tv_title.setText(questionnaire.getQuestionnaireTitle());
        tv_subtitle.setText(questionnaire.getQuestionnaireSubtitle());
        tv_description.setText(questionnaire.getQuestionnaireDescription());
        questions=questionnaire.getQuestions();
        questionNum=questions.size();

        //加载问题
        initPage();




        //返回按钮
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(DoResearch.this);
                builder.setMessage("您是否要放弃本次问卷填写？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AlertDialog.Builder builderConfirm=new AlertDialog.Builder(DoResearch.this);
                        builderConfirm.setMessage("是否要保存此次填写内容？（如选择“是”，会覆盖上次保存内容）");
                        builderConfirm.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveUnfinishResult();
                                saveLastMission();
                                dialogInterface.dismiss();
                                DoResearch.this.finish();
                            }
                        });
                        builderConfirm.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveLastMission();
                                dialogInterface.dismiss();
                                DoResearch.this.finish();
                            }
                        });
                        AlertDialog dialog=builderConfirm.create();
                        dialog.show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });




    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(haveUnfinishResult.equals("true")&&!haveShow){
            loadUnFinishResult();
            haveShow=true;
        }
    }


    //创建测试用数据
    public void inputData(){
        //每个选项初始化
            //第一个问题
        QuestionOptionVO option1=new QuestionOptionVO();
        option1.setOptionOrder(0);
        option1.setOption("男");

        QuestionOptionVO option2=new QuestionOptionVO();
        option2.setOption("女");
        option2.setOptionOrder(1);

        List<QuestionOptionVO> listOption1=new ArrayList<>();
        listOption1.add(option1);
        listOption1.add(option2);

            //第二个问题
        QuestionOptionVO option3=new QuestionOptionVO();
        option3.setOptionOrder(0);
        option3.setOption("经常运动");

        QuestionOptionVO option4=new QuestionOptionVO();
        option4.setOptionOrder(1);
        option4.setOption("偶尔运动");

        QuestionOptionVO option5=new QuestionOptionVO();
        option5.setOptionOrder(2);
        option5.setOption("很少运动");

        List<QuestionOptionVO> listOption2=new ArrayList<>();
        listOption2.add(option3);
        listOption2.add(option4);
        listOption2.add(option5);


            //  第三个问题
        QuestionOptionVO option6=new QuestionOptionVO();
        option6.setOptionOrder(0);
        option6.setOption("咳嗽");

        QuestionOptionVO option7=new QuestionOptionVO();
        option7.setOptionOrder(1);
        option7.setOption("咽喉痛");

        QuestionOptionVO option8=new QuestionOptionVO();
        option8.setOptionOrder(2);
        option8.setOption("流鼻涕");

        QuestionOptionVO option9=new QuestionOptionVO();
        option9.setOptionOrder(3);
        option9.setOption("频繁打喷嚏");

        List<QuestionOptionVO> listOption3=new ArrayList<>();
        listOption3.add(option6);
        listOption3.add(option7);
        listOption3.add(option8);
        listOption3.add(option9);

            //  第四个问题
        QuestionOptionVO option10=new QuestionOptionVO();
        option10.setOptionOrder(0);
        option10.setOption("城市");

        QuestionOptionVO option11=new QuestionOptionVO();
        option11.setOptionOrder(1);
        option11.setOption("农村");

        QuestionOptionVO option12=new QuestionOptionVO();
        option12.setOptionOrder(2);
        option12.setOption("城乡结合部");

        List<QuestionOptionVO> listOption4=new ArrayList<>();
        listOption4.add(option10);
        listOption4.add(option11);
        listOption4.add(option12);

             //  第五个问题
        QuestionOptionVO option13=new QuestionOptionVO();
        List<QuestionOptionVO> listOption5=new ArrayList<>();
        listOption5.add(option13);

            //  第六个问题
        QuestionOptionVO option14=new QuestionOptionVO();
        option14.setOptionOrder(0);
        option14.setOption("市");

        QuestionOptionVO option15=new QuestionOptionVO();
        option15.setOptionOrder(1);
        option15.setOption("县");

        QuestionOptionVO option16=new QuestionOptionVO();
        option16.setOptionOrder(2);
        option16.setOption("区");

        List<QuestionOptionVO> listOption6=new ArrayList<>();
        listOption6.add(option14);
        listOption6.add(option15);
        listOption6.add(option16);


        //每个题目初始化
            //第一个问题
        QuestionVO question1=new QuestionVO();
        question1.setQuestionContext("您的性别：");
        question1.setQuestionType("单选题");
        question1.setMust(true);
        question1.setOptions(listOption1);

            //第二个问题
        QuestionVO question2=new QuestionVO();
        question2.setQuestionContext("您平时的运动量如何：");
        question2.setQuestionType("单选题");
        question2.setMust(false);
        question2.setOptions(listOption2);

            //第三个问题
        QuestionVO question3=new QuestionVO();
        question3.setQuestionContext("您最近是否出现以下症状：");
        question3.setQuestionType("多选题");
        question3.setMust(false);
        question3.setOptions(listOption3);

            //第四个问题
        QuestionVO question4=new QuestionVO();
        question4.setQuestionContext("您居住在以下哪个区域：");
        question4.setQuestionType("下拉选择题");
        question4.setMust(true);
        question4.setOptions(listOption4);

            // 第五个问题
        QuestionVO question5=new QuestionVO();
        question5.setQuestionContext("您对我们的工作有什么建议：");
        question5.setQuestionType("单项填空题");
        question5.setMust(false);
        question5.setOptions(listOption5);

            //  第六个问题
        QuestionVO question6=new QuestionVO();
        question6.setQuestionContext("您的居住地：");
        question6.setQuestionType("多项填空题");
        question6.setMust(false);
        question6.setOptions(listOption6);

        //把问题加入List
        List<QuestionVO> listQuestion=new ArrayList<>();
        listQuestion.add(question1);
        listQuestion.add(question2);
        listQuestion.add(question3);
        listQuestion.add(question4);
        listQuestion.add(question5);
        listQuestion.add(question6);
        listQuestion.add(question6);



        //问卷初始化
        questionnaire=new DisplayQuestionnaireVO();
        questionnaire.setQuestionnaireId(new Long(1));
        questionnaire.setQuestionnaireTitle("调查问卷A");
        questionnaire.setQuestionnaireSubtitle("开发测试");
        questionnaire.setQuestionnaireDescription("开发时使用的测试数据");
        questionnaire.setQuestions(listQuestion);

    }

    //加载测试用文件
    public void testLoadFile(){
        MyFile myFile=new MyFile("1",this);
        String temp=myFile.readFile();
        DisplayQuestionnaireVO tempBean=JSON.parseObject(temp,DisplayQuestionnaireVO.class);
        questionnaire=tempBean;
    }

    //加载问卷文件
    public void loadFile(String fileName){
        MyFile myFile=new MyFile(fileName,this);
        String temp=myFile.readFile();
        DisplayQuestionnaireVO tempBean=JSON.parseObject(temp,DisplayQuestionnaireVO.class);
        questionnaire=tempBean;
    }


    //初始化页面
    public void initPage(){
        for (int i=0;i<questionNum;i++){
            View questionView= LayoutInflater.from(DoResearch.this).inflate(R.layout.each_question,null);
            TextView tv_questionTitle= (TextView) questionView.findViewById(R.id.textView_each_question_title);
            RecyclerView rv_questions= (RecyclerView) questionView.findViewById(R.id.recycleView_each_question);

            //是否是必选题
            String content=(i+1)+"."+questions.get(i).getQuestionContext();
            String w=content;
            if(questions.get(i).getMust()==true){
                w=w+"*";
            }
            int start=content.length();
            int end=w.length();

            Spannable word=new SpannableString(w);
            word.setSpan(new ForegroundColorSpan(Color.RED),start,end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_questionTitle.setText(word);


            //判断题型
            switch (questions.get(i).getQuestionType()){
                case "单选题":
                    SingleChoiceAdapter singleAdapter=new SingleChoiceAdapter(questions.get(i).getOptions(),DoResearch.this);
                    LinearLayoutManager singleManager=new LinearLayoutManager(DoResearch.this);
                    rv_questions.setLayoutManager(singleManager);
                    rv_questions.setAdapter(singleAdapter);
                    break;
                case "多选题":
                    MultipleChoiceAdapter multipleAdapter=new MultipleChoiceAdapter(questions.get(i).getOptions(),DoResearch.this);
                    LinearLayoutManager mulManager=new LinearLayoutManager(DoResearch.this);
                    rv_questions.setLayoutManager(mulManager);
                    rv_questions.setAdapter(multipleAdapter);
                    break;

                case "下拉选择题":
                    SpinnerChoiceAdapter spinnerAdapter=new SpinnerChoiceAdapter(questions.get(i).getOptions(),DoResearch.this);
                    LinearLayoutManager spinnerManager=new LinearLayoutManager(DoResearch.this);
                    rv_questions.setLayoutManager(spinnerManager);
                    rv_questions.setAdapter(spinnerAdapter);
                    break;

                case "单项填空题":
                    SingleLineBlankAdapter singleBlankAdapter=new SingleLineBlankAdapter();
                    LinearLayoutManager singleBlankManager=new LinearLayoutManager(DoResearch.this);
                    rv_questions.setLayoutManager(singleBlankManager);
                    rv_questions.setAdapter(singleBlankAdapter);
                    break;

                case "多项填空题" :
                    MultipleLineBlankAdapter mulBlankAdapter=new MultipleLineBlankAdapter(questions.get(i).getOptions());
                    LinearLayoutManager mulBlankManager=new LinearLayoutManager(DoResearch.this);
                    rv_questions.setLayoutManager(mulBlankManager);
                    rv_questions.setAdapter(mulBlankAdapter);
                    break;

                case "图片选择题":
                    PictureSelectAdapter pictureSingleAdapter=new PictureSelectAdapter(questions.get(i).getOptions(),DoResearch.this,questionnaire.getQuestionnaireId());
                    LinearLayoutManager imageSingle=new LinearLayoutManager(DoResearch.this);
                    rv_questions.setLayoutManager(imageSingle);
                    rv_questions.setAdapter(pictureSingleAdapter);
                    break;

                case "未知题型":
                    Toast.makeText(DoResearch.this,"问卷题目加载出错",Toast.LENGTH_SHORT).show();
                    tv_questionTitle.setText((i+1)+".题目加载错误");
                    break;

                default:
                    Toast.makeText(DoResearch.this,"问卷题目加载出错",Toast.LENGTH_SHORT).show();
                    tv_questionTitle.setText((i+1)+".题目加载错误");
                    break;
            }
            layout_questions.addView(questionView);
            recycleViewList.add(rv_questions);

        }
        View commitView=LayoutInflater.from(DoResearch.this).inflate(R.layout.item_commit_button,null);
        Button bt_commit= (Button) commitView.findViewById(R.id.button_item_commit_commit);
        layout_questions.addView(commitView);
        //载入保存的内容


        //上传按钮
        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<questionNum;i++){
                    RecyclerView rv_questions=recycleViewList.get(i);
                    AnswerDetailVO answerDetailEntity=new AnswerDetailVO();
                    List<String> tempList=new ArrayList<String>();
                    switch (questions.get(i).getQuestionType()){
                        case "单选题":
                            tempList=((SingleChoiceAdapter)rv_questions.getAdapter()).getResult();
                            answerDetailEntity.setQuestionType("单选题");
//                            answerDetailEntity.setQuestionId(((SingleChoiceAdapter) rv_questions.getAdapter()).getQuestionId());
                            break;
                        case "多选题":
                            tempList=((MultipleChoiceAdapter)rv_questions.getAdapter()).getResult();
                            answerDetailEntity.setQuestionType("多选题");
//                            answerDetailEntity.setQuestionId(((MultipleChoiceAdapter) rv_questions.getAdapter()).getQuestionId());
                            break;
                        case "下拉选择题":
                            tempList=((SpinnerChoiceAdapter)rv_questions.getAdapter()).getResult();
                            answerDetailEntity.setQuestionType("下拉选择题");
//                            answerDetailEntity.setQuestionId(((SpinnerChoiceAdapter) rv_questions.getAdapter()).getQuestionId());
                            break;
                        case "单项填空题":
                            tempList=((SingleLineBlankAdapter)rv_questions.getAdapter()).getResult();
                            answerDetailEntity.setQuestionType("单项填空题");
//                            answerDetailEntity.setQuestionId(((SingleLineBlankAdapter) rv_questions.getAdapter()).getQuestionId());
                            break;
                        case "多项填空题":
                            tempList=((MultipleLineBlankAdapter)rv_questions.getAdapter()).getResult();
                            answerDetailEntity.setQuestionType("多项填空题");
//                            answerDetailEntity.setQuestionId(((MultipleLineBlankAdapter) rv_questions.getAdapter()).getQuestionId());
                            break;
                        case "图片选择题":
                            break;
                        case "未知题型":
                            break;
                        default :
                            break;
                    }
                    //必选题存在空
                    if ((tempList==null)&&(questions.get(i).getMust()==true)){
                        Toast.makeText(DoResearch.this,"您有必选题未填，请确认后重试",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(tempList==null){
                        tempList=new ArrayList<String>();
                    }
                    answerDetailEntity.setAnswer(tempList);
                    answerDetailEntity.setQuestionId(questions.get(i).getQuestionId());

                    resultList.add(answerDetailEntity);
                }
                //将结果加入Result
                if(locationFind.haveResult()){

                    answerPaper.setQuestionnaireId(questionnaire.getQuestionnaireId());
                    answerPaper.setResearchId(researchIdFromMain);
                    answerPaper.setLongitude(locationFind.getLongitude());
                    answerPaper.setLatitude(locationFind.getLatitude());
                    answerPaper.setFillAnswerTime(new Date(Calendar.getInstance().getTimeInMillis()));
                    answerPaper.setAnswerDetailVOList(resultList);
                    saveResult();

                }
                else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(DoResearch.this);
                    builder.setTitle("提交确认");
                    builder.setMessage("您的定位信息暂时无法获取，是否现在提交？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            answerPaper.setQuestionnaireId(questionnaire.getQuestionnaireId());
                            answerPaper.setResearchId(researchIdFromMain);
                            answerPaper.setLongitude(locationFind.getLongitude());
                            answerPaper.setLatitude(locationFind.getLatitude());
                            answerPaper.setFillAnswerTime(new Date(Calendar.getInstance().getTimeInMillis()));
                            answerPaper.setAnswerDetailVOList(resultList);
                            saveResult();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
        });
    }

    //检查权限
    public void checkPermission(){
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(DoResearch.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(DoResearch.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(DoResearch.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(DoResearch.this,permissions,1);
        }

        else {
            requestLocation();
        }
    }

    //设置广播适配器
    public void setInfilter(){
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        receiver=new NetworkChangeReceiver();
        registerReceiver(receiver,intentFilter);
    }

    //请求地址
    public void requestLocation(){
        locationFind=new MyLocationFind(DoResearch.this.getApplicationContext(),receiver.isHaveNetwork());
        locationFind.startService();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(DoResearch.this,"有权限尚未被批准",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }
                else {
                    Toast.makeText(DoResearch.this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationFind.stopService();
        unregisterReceiver(receiver);
    }


    public void saveResult(){
        JSONArray oldArray=loadResultFile();
        MyFile mySaveResult=new MyFile(questionnaire.getQuestionnaireId()+"NotUpload",DoResearch.this);
        List<AnswerPaperVO> list=new ArrayList<>();
        if(oldArray!=null){
            list= parseArray(oldArray.toString(),AnswerPaperVO.class);
        }
        list.add(answerPaper);
        boolean succ=mySaveResult.writeFile(JSON.toJSONString(list),false);
        if(succ){
            MyFile updateToFileList=new MyFile("fileList",DoResearch.this);
            List<QuestionnaireSave> saveList= parseArray(updateToFileList.readFile(),QuestionnaireSave.class);
            for (int i=0;i<saveList.size();i++){
                if(saveList.get(i).getQuestionnaireId()==questionnaire.getQuestionnaireId()){
                    saveList.get(i).setFinishNum(saveList.get(i).getFinishNum()+1);
                    if(haveUnfinishResult.equals("true")){
                        MyFile myFile=new MyFile(DoResearch.this);
                        myFile.deleteFile(questionnaire.getQuestionnaireId().toString()+"NotFinish",DoResearch.this);
                        saveList.get(i).setUnfinishResult(false);
                    }
                    updateToFileList.writeFile(JSON.toJSONString(saveList),false);
                    break;
                }
            }


            Toast.makeText(DoResearch.this,"保存成功",Toast.LENGTH_SHORT).show();
            updateNotUploadList(questionnaire.getQuestionnaireId()+"");
            saveLastMission();
            DoResearch.this.finish();
        }
        else {
            Toast.makeText(DoResearch.this,"保存失败",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            AlertDialog.Builder builder=new AlertDialog.Builder(DoResearch.this);
            builder.setMessage("您是否要放弃本次问卷填写？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    AlertDialog.Builder builderConfirm=new AlertDialog.Builder(DoResearch.this);
                    builderConfirm.setMessage("是否要保存此次填写内容？（如选择“是”，会覆盖上次保存内容）");
                    builderConfirm.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveUnfinishResult();
                            saveLastMission();
                            dialogInterface.dismiss();
                            DoResearch.this.finish();
                        }
                    });
                    builderConfirm.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveLastMission();
                            dialogInterface.dismiss();
                            DoResearch.this.finish();
                        }
                    });
                    AlertDialog dialog=builderConfirm.create();
                    dialog.show();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public JSONArray loadResultFile(){
        MyFile myFile=new MyFile(questionnaire.getQuestionnaireId()+"NotUpload",DoResearch.this);
        String data=myFile.readFile();
        if(!data.isEmpty()){
            JSONArray jsonArray=null;
            try {
                jsonArray=new JSONArray(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;
        }
        else
            return null;
    }


    //文件格式jsonArray
    public void updateNotUploadList(String questionnaireId){
        MyFile myFile=new MyFile("notUploadFileList",DoResearch.this);
        String data=myFile.readFile();
        JSONArray jsonArray=null;
        if(!data.isEmpty()){
            try {
                jsonArray=new JSONArray(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            jsonArray=new JSONArray();
        }
        boolean havaSame=false;
        for (int i=0;i<jsonArray.length();i++){
            try {
                if(jsonArray.get(i).toString().equals(questionnaireId)){
                    havaSame=true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(!havaSame){
            jsonArray.put(questionnaireId);
            myFile.writeFile(jsonArray.toString(),false);
        }
    }


    private void saveLastMission(){
        MyFile myFile=new MyFile("lastMission",DoResearch.this);
        String date=myFile.readFile();
        List<String> listNew=new ArrayList<>();
        if(!date.isEmpty()){
            List<String> listOld= parseArray(date,String.class);
            List<String> tempList=new ArrayList<>();
            boolean havaSame=false;
            String sameId="";
            for(int i=0;i<listOld.size();i++){
                if(!listOld.get(i).equals(questionnaire.getQuestionnaireId().toString())){
                    tempList.add(listOld.get(i));
                }
                else {
                    havaSame=true;
                    sameId=listOld.get(i);
                }

            }
            if(havaSame){
                listNew.add(sameId);
                for (int i=0;i<listOld.size();i++){
                    if(!listOld.get(i).equals(sameId)){
                        listNew.add(listOld.get(i));
                        break;
                    }
                }
            }
            else {
                listNew.add(questionnaire.getQuestionnaireId().toString());
                listNew.add(listOld.get(0));
            }
        }
        else {
            listNew.add(questionnaire.getQuestionnaireId().toString());
        }
        myFile.writeFile(JSON.toJSONString(listNew),false);
    }

    private void loadUnFinishResult(){
        MyFile myFile=new MyFile(questionnaire.getQuestionnaireId().toString()+"NotFinish",DoResearch.this);
//        String data=myFile.readFile();
//        if(!data.isEmpty()){
//            return;
//        }
        List<TempResultSave> list= parseArray(myFile.readFile(),TempResultSave.class);
        for (int i=0;i<list.size();i++){
            int position=list.get(i).getPosition();
            RecyclerView.Adapter adapter=recycleViewList.get(position).getAdapter();
            switch (questions.get(position).getQuestionType()){
                case "单选题":
                    ((SingleChoiceAdapter)adapter).loadResult(list.get(i).getContent());
                    break;
                case "多选题":
                    ((MultipleChoiceAdapter)adapter).loadResult(list.get(i).getContent());
                    break;
                case "下拉选择题":
                    ((SpinnerChoiceAdapter)adapter).loadResult(list.get(i).getContent());
                    break;
                case "单项填空题":
                    ((SingleLineBlankAdapter)adapter).loadResult(list.get(i).getContent());
                    break;
                case "多项填空题":
                    ((MultipleLineBlankAdapter)adapter).loadResult(list.get(i).getContent());
                    break;
                case "图片选择题":
                    break;
                case "未知题型":
                    break;
                default :
                    break;
            }
        }

    }

    private void saveUnfinishResult(){
        MyFile myFile=new MyFile(questionnaire.getQuestionnaireId().toString()+"NotFinish",DoResearch.this);
        List<TempResultSave> dataList=new ArrayList<>();
        for (int i=0;i<questionNum;i++){
            RecyclerView rv_questions=recycleViewList.get(i);
            TempResultSave resultSave=new TempResultSave();
            List<String> tempList=new ArrayList<String>();
            switch (questions.get(i).getQuestionType()){
                case "单选题":
                    tempList=((SingleChoiceAdapter)rv_questions.getAdapter()).getResult();
                    break;
                case "多选题":
                    tempList=((MultipleChoiceAdapter)rv_questions.getAdapter()).getResult();
                    break;
                case "下拉选择题":
                    tempList=((SpinnerChoiceAdapter)rv_questions.getAdapter()).getResult();
                    break;
                case "单项填空题":
                    tempList=((SingleLineBlankAdapter)rv_questions.getAdapter()).getResult();
                    break;
                case "多项填空题":
                    tempList=((MultipleLineBlankAdapter)rv_questions.getAdapter()).getResult();
                    break;
                case "图片选择题":
                    break;
                case "未知题型":
                    break;
                default :
                    break;
            }
            if(tempList!=null){
                resultSave.setPosition(i);
                resultSave.setContent(tempList);
                dataList.add(resultSave);
            }
        }
        if(dataList.size()>0){
            myFile.writeFile(JSON.toJSONString(dataList),false);
            MyFile fileListOP=new MyFile("fileList",DoResearch.this);
            List<QuestionnaireSave> fileList=JSON.parseArray(fileListOP.readFile(),QuestionnaireSave.class);
            for (int i=0;i<fileList.size();i++){
                if(fileList.get(i).getQuestionnaireId()==questionnaire.getQuestionnaireId()){
                    fileList.get(i).setUnfinishResult(true);
                    break;
                }
            }
            fileListOP.writeFile(JSON.toJSONString(fileList),false);
        }

    }




}
