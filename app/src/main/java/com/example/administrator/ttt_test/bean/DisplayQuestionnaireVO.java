package com.example.administrator.ttt_test.bean;

import java.util.List;

/**
 * Created by Acer on 2017/4/6.
 */

public class DisplayQuestionnaireVO {

    private Long questionnaireId;//问卷id 创建时默认为0
    private String questionnaireTitle;//问卷标题 (必填字段，不得为空)
    private String questionnaireSubtitle;//问卷副标题
    private String questionnaireDescription;//问卷描述

    /*问卷题目信息*/
    private List<QuestionVO> questions;


    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionnaireTitle() {
        return questionnaireTitle;
    }

    public void setQuestionnaireTitle(String questionnaireTitle) {
        this.questionnaireTitle = questionnaireTitle;
    }

    public String getQuestionnaireSubtitle() {
        return questionnaireSubtitle;
    }

    public void setQuestionnaireSubtitle(String questionnaireSubtitle) {
        this.questionnaireSubtitle = questionnaireSubtitle;
    }

    public String getQuestionnaireDescription() {
        return questionnaireDescription;
    }

    public void setQuestionnaireDescription(String questionnaireDescription) {
        this.questionnaireDescription = questionnaireDescription;
    }

    public List<QuestionVO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionVO> questions) {
        this.questions = questions;
    }
}
