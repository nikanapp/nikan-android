package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Recommend;

import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/28.
 */
public class RecommendResult extends ProcessResult {

    private List<Recommend> recommenders;

    public List<Recommend> getRecommenders() {
        return recommenders;
    }

    public void setRecommenders(List<Recommend> recommenders) {
        this.recommenders = recommenders;
    }
}
