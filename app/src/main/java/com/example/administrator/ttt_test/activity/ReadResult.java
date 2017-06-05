package com.example.administrator.ttt_test.activity;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.example.administrator.ttt_test.bean.Result;
import com.example.administrator.ttt_test.broadcast.NetworkChangeReceiver;
import com.example.administrator.ttt_test.fragment.local.adapter.MultipleChoiceAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.MultipleLineBlankAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.PictureSelectAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.SingleChoiceAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.SingleLineBlankAdapter;
import com.example.administrator.ttt_test.fragment.local.adapter.SpinnerChoiceAdapter;
import com.example.administrator.ttt_test.util.file.MyFile;
import com.example.administrator.ttt_test.util.location.MyLocationFind;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseArray;

public class ReadResult extends AppCompatActivity {
    private DisplayQuestionnaireVO questionnaire=new DisplayQuestionnaireVO();
    private List<QuestionVO> questions=new ArrayList<>();
    private List<QuestionOptionVO> options=new ArrayList<>();
    private List<RecyclerView> recycleViewList=new ArrayList<>();
    private int questionNum=0;
    private int AnswerPaperNum=0;
    private Result result=new Result();
    private List<AnswerPaperVO> answerPaperList=new ArrayList<>();
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

        researchIdFromMain=new Long(getIntent().getStringExtra("researchId"));
        loadFile(getIntent().getStringExtra("fileId"));

        tv_title= (TextView) this.findViewById(R.id.textView_do_research_title);
        tv_subtitle= (TextView) this.findViewById(R.id.textView_do_research_subtitle);
        tv_description= (TextView) this.findViewById(R.id.textView_do_research_description);
        tv_back= (TextView) this.findViewById(R.id.textView_do_research_back);
        layout_questions= (LinearLayout) this.findViewById(R.id.layout_questions);

        if(questionnaire.getQuestionnaireTitle().length()>10){
            tv_title.setTextSize(21);
        }
        tv_title.setText(questionnaire.getQuestionnaireTitle());
        tv_subtitle.setText(questionnaire.getQuestionnaireSubtitle());
        tv_description.setText(questionnaire.getQuestionnaireDescription());
        questions=questionnaire.getQuestions();
        questionNum=questions.size();




        initPage();
        loadResultFile();


        //返回按钮
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadResult.this.finish();
            }
        });

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!haveShow){
            loadUnFinishResult();
            haveShow=true;
        }
    }


    public void loadFile(String fileName){
        MyFile myFile=new MyFile(fileName,this);
        String temp=myFile.readFile();
        DisplayQuestionnaireVO tempBean= JSON.parseObject(temp,DisplayQuestionnaireVO.class);
        questionnaire=tempBean;
    }

    //初始化页面
    public void initPage(){
        for (int i=0;i<questionNum;i++){
            View questionView= LayoutInflater.from(ReadResult.this).inflate(R.layout.each_question,null);
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
                    SingleChoiceAdapter singleAdapter=new SingleChoiceAdapter(questions.get(i).getOptions(),ReadResult.this);
                    LinearLayoutManager singleManager=new LinearLayoutManager(ReadResult.this);
                    rv_questions.setLayoutManager(singleManager);
                    rv_questions.setAdapter(singleAdapter);
                    break;
                case "多选题":
                    MultipleChoiceAdapter multipleAdapter=new MultipleChoiceAdapter(questions.get(i).getOptions(),ReadResult.this);
                    LinearLayoutManager mulManager=new LinearLayoutManager(ReadResult.this);
                    rv_questions.setLayoutManager(mulManager);
                    rv_questions.setAdapter(multipleAdapter);
                    break;

                case "下拉选择题":
                    SpinnerChoiceAdapter spinnerAdapter=new SpinnerChoiceAdapter(questions.get(i).getOptions(),ReadResult.this);
                    LinearLayoutManager spinnerManager=new LinearLayoutManager(ReadResult.this);
                    rv_questions.setLayoutManager(spinnerManager);
                    rv_questions.setAdapter(spinnerAdapter);
                    break;

                case "单项填空题":
                    SingleLineBlankAdapter singleBlankAdapter=new SingleLineBlankAdapter();
                    LinearLayoutManager singleBlankManager=new LinearLayoutManager(ReadResult.this);
                    rv_questions.setLayoutManager(singleBlankManager);
                    rv_questions.setAdapter(singleBlankAdapter);
                    break;

                case "多项填空题" :
                    MultipleLineBlankAdapter mulBlankAdapter=new MultipleLineBlankAdapter(questions.get(i).getOptions());
                    LinearLayoutManager mulBlankManager=new LinearLayoutManager(ReadResult.this);
                    rv_questions.setLayoutManager(mulBlankManager);
                    rv_questions.setAdapter(mulBlankAdapter);
                    break;

                case "图片选择题":
                    PictureSelectAdapter pictureSingleAdapter=new PictureSelectAdapter(questions.get(i).getOptions(),ReadResult.this,questionnaire.getQuestionnaireId());
                    LinearLayoutManager imageSingle=new LinearLayoutManager(ReadResult.this);
                    rv_questions.setLayoutManager(imageSingle);
                    rv_questions.setAdapter(pictureSingleAdapter);
                    break;

                case "未知题型":
                    Toast.makeText(ReadResult.this,"问卷题目加载出错",Toast.LENGTH_SHORT).show();
                    tv_questionTitle.setText((i+1)+".题目加载错误");
                    break;

                default:
                    Toast.makeText(ReadResult.this,"问卷题目加载出错",Toast.LENGTH_SHORT).show();
                    tv_questionTitle.setText((i+1)+".题目加载错误");
                    break;
            }
            layout_questions.addView(questionView);
            recycleViewList.add(rv_questions);

        }
        View commitView=LayoutInflater.from(ReadResult.this).inflate(R.layout.item_commit_button,null);
        Button bt_commit= (Button) commitView.findViewById(R.id.button_item_commit_commit);
        bt_commit.setText("下一份");

        layout_questions.addView(commitView);



        //上传按钮
        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AnswerPaperNum<answerPaperList.size()-1){
                    AnswerPaperNum++;
                    loadUnFinishResult();

                }
                else {
                    Toast.makeText(ReadResult.this,"这已经是最后一份了",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void loadResultFile(){
        MyFile myFile=new MyFile(questionnaire.getQuestionnaireId()+"NotUpload",ReadResult.this);
        answerPaperList= parseArray(myFile.readFile(),AnswerPaperVO.class);

    }


    private void loadUnFinishResult(){
        List<AnswerDetailVO> list= answerPaperList.get(AnswerPaperNum).getAnswerDetailVOList();
        for (int i=0;i<list.size();i++){
            int position=i;
            RecyclerView.Adapter adapter=recycleViewList.get(position).getAdapter();
            if(list.get(i).getAnswer().size()>0) {
                switch (questions.get(position).getQuestionType()) {
                    case "单选题":
                        ((SingleChoiceAdapter) adapter).loadResult(list.get(i).getAnswer());
                        break;
                    case "多选题":
                        ((MultipleChoiceAdapter) adapter).loadResult(list.get(i).getAnswer());
                        break;
                    case "下拉选择题":
                        ((SpinnerChoiceAdapter) adapter).loadResult(list.get(i).getAnswer());
                        break;
                    case "单项填空题":
                        ((SingleLineBlankAdapter) adapter).loadResult(list.get(i).getAnswer());
                        break;
                    case "多项填空题":
                        ((MultipleLineBlankAdapter) adapter).loadResult(list.get(i).getAnswer());
                        break;
                    case "图片选择题":
                        break;
                    case "未知题型":
                        break;
                    default:
                        break;
                }
            }
        }

    }

}
