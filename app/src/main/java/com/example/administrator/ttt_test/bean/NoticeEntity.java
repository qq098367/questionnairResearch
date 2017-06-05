package com.example.administrator.ttt_test.bean;

import java.util.Date;

/**
 * Created by Acer on 2017/5/2.
 */

public class NoticeEntity {
    private Long noticeId;
    private String noticeTitle;
    private String noticeContent;
    private Date noticeLaunchDate;
    private String createUnit;


    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public Date getNoticeLaunchDate() {
        return noticeLaunchDate;
    }

    public void setNoticeLaunchDate(Date noticeLaunchDate) {
        this.noticeLaunchDate = noticeLaunchDate;
    }

    public String getCreateUnit() {
        return createUnit;
    }

    public void setCreateUnit(String createUnit) {
        this.createUnit = createUnit;
    }
}
