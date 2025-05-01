package com.ssafy.yumTree.model.dto;

import java.util.Date;

public class CommunityDto {
    private int cmNumber;
    private String cmTitle;
    private String cmContent;
    private String cmUserNumber;
    private Date cmRegdate;
    private int cmBlameCnt;
    private boolean cmBlockYn;

    public int getCmNumber() {
        return cmNumber;
    }

    public void setCmNumber(int cmNumber) {
        this.cmNumber = cmNumber;
    }

    public String getCmTitle() {
        return cmTitle;
    }

    public void setCmTitle(String cmTitle) {
        this.cmTitle = cmTitle;
    }

    public String getCmContent() {
        return cmContent;
    }

    public void setCmContent(String cmContent) {
        this.cmContent = cmContent;
    }

    public String getCmUserNumber() {
        return cmUserNumber;
    }

    public void setCmUserNumber(String cmUserNumber) {
        this.cmUserNumber = cmUserNumber;
    }

    public Date getCmRegdate() {
        return cmRegdate;
    }

    public void setCmRegdate(Date cmRegdate) {
        this.cmRegdate = cmRegdate;
    }

    public int getCmBlameCnt() {
        return cmBlameCnt;
    }

    public void setCmBlameCnt(int cmBlameCnt) {
        this.cmBlameCnt = cmBlameCnt;
    }

    public boolean isCmBlockYn() {
        return cmBlockYn;
    }

    public void setCmBlockYn(boolean cmBlockYn) {
        this.cmBlockYn = cmBlockYn;
    }
}
