package com.bloomlife.android.bean;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/22.
 */
public class FailureResult {

    private int statusCode;
    private Integer subCode;
    private String subDes;

    public FailureResult(int statusCode, Integer subCode, String subDes){
        this.statusCode = statusCode;
        this.subCode = subCode;
        this.subDes = subDes;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getSubCode() {
        return subCode;
    }

    public void setSubCode(Integer subCode) {
        this.subCode = subCode;
    }

    public String getSubDes() {
        return subDes;
    }

    public void setSubDes(String subDes) {
        this.subDes = subDes;
    }
}
