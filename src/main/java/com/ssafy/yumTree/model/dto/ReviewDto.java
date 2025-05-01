package com.ssafy.yumTree.model.dto;

import java.util.Date;

public class ReviewDto {
    private int rvNumber;
    private int rvCmNumber;
    private String rvContent;
    private Date rvRegdate;
    private int rvUserNumber;

    public int getRvNumber() {
        return rvNumber;
    }

    public void setRvNumber(int rvNumber) {
        this.rvNumber = rvNumber;
    }

    public int getRvCmNumber() {
        return rvCmNumber;
    }

    public void setRvCmNumber(int rvCmNumber) {
        this.rvCmNumber = rvCmNumber;
    }

    public String getRvContent() {
        return rvContent;
    }

    public void setRvContent(String rvContent) {
        this.rvContent = rvContent;
    }

    public Date getRvRegdate() {
        return rvRegdate;
    }

    public void setRvRegdate(Date rvRegdate) {
        this.rvRegdate = rvRegdate;
    }

    public int getRvUserNumber() {
        return rvUserNumber;
    }

    public void setRvUserNumber(int rvUserNumber) {
        this.rvUserNumber = rvUserNumber;
    }
}
