package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/27.
 */
public class EditUserInfoResult extends ProcessResult{

    public static final int SUCC = 0;
    public static final int NAME_ALREADY_USED = 1;

    private int statecode;

    public int getStatecode() {
        return statecode;
    }

    public void setStatecode(int statecode) {
        this.statecode = statecode;
    }
}
