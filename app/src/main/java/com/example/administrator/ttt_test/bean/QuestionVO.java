package com.example.administrator.ttt_test.bean;

import java.util.List;

/**
 * Created by Acer on 2017/4/6.
 */

public class QuestionVO {
    private Long questionId;//题目ID
    private String questionContext;//题目
    private String questionType;//题目类型
    private String questionDescription;//题目描述
    private Boolean isMust;//是否必做题

    private List<QuestionOptionVO> options;//题目选项信息


    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionContext() {
        return questionContext;
    }

    public void setQuestionContext(String questionContext) {
        this.questionContext = questionContext;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public Boolean getMust() {
        return isMust;
    }

    public void setMust(Boolean must) {
        isMust = must;
    }

    public List<QuestionOptionVO> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionVO> options) {
        this.options = options;
    }
}
