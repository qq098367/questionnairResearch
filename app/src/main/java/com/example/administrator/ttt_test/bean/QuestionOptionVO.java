package com.example.administrator.ttt_test.bean;

/**
 * Created by Acer on 2017/4/6.
 */

public class QuestionOptionVO {

    private Long questionId;
    private int optionOrder;//选项顺序
    private String option;//选项


    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public int getOptionOrder() {

        return optionOrder;
    }

    public void setOptionOrder(int optionOrder) {
        this.optionOrder = optionOrder;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
