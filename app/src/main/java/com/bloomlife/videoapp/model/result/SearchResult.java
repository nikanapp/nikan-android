package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.User;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/6.
 */
public class SearchResult extends ProcessResult {

    private List<User> users;
    private String pagecursor;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getPagecursor() {
        return pagecursor;
    }

    public void setPagecursor(String pagecursor) {
        this.pagecursor = pagecursor;
    }
}
