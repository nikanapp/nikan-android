package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Story;

import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/3.
 */
public class GetStoryListResult extends ProcessResult {

    private List<Story> hoststorys;
    private String pagecursor;

    public List<Story> getHoststorys() {
        return hoststorys;
    }

    public void setHoststorys(List<Story> hoststorys) {
        this.hoststorys = hoststorys;
    }

    public String getPagecursor() {
        return pagecursor;
    }

    public void setPagecursor(String pagecursor) {
        this.pagecursor = pagecursor;
    }
}
