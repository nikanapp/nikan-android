package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Fans;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class FansListResult extends ProcessResult {

    private List<Fans> users;
    private String pagecursor;


    public List<Fans> getUsers() {
        return users;
    }

    public void setUsers(List<Fans> users) {
        this.users = users;
    }

    public String getPagecursor() {
        return pagecursor;
    }

    public void setPagecursor(String pagecursor) {
        this.pagecursor = pagecursor;
    }
}
