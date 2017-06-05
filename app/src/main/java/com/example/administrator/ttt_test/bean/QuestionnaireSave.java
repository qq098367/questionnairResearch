package com.example.administrator.ttt_test.bean;

import java.util.Date;

/**
 * Created by Acer on 2017/4/23.
 */

public class QuestionnaireSave {
    private Long questionnaireId;//问卷id 创建时默认为0
    private String questionnaireTitle;//问卷标题 (必填字段，不得为空)
    private Date publishTime;
    private int finishNum;
    private Long researchId;
    private boolean UnfinishResult;

    public boolean isUnfinishResult() {
        return UnfinishResult;
    }

    public void setUnfinishResult(boolean unfinishResult) {
        UnfinishResult = unfinishResult;
    }

    public Long getResearchId() {
        return researchId;
    }

    public void setResearchId(Long reearchId) {
        this.researchId = reearchId;
    }

    public int getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(int finishNum) {
        this.finishNum = finishNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionnaireSave)) return false;

        QuestionnaireSave that = (QuestionnaireSave) o;

        if (!getQuestionnaireId().equals(that.getQuestionnaireId())) return false;
        if (!getQuestionnaireTitle().equals(that.getQuestionnaireTitle())) return false;
        return getPublishTime().equals(that.getPublishTime());

    }

    @Override
    public int hashCode() {
        int result = getQuestionnaireId().hashCode();
        result = 31 * result + getQuestionnaireTitle().hashCode();
        result = 31 * result + getPublishTime().hashCode();
        return result;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

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
}
