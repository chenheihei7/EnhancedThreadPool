package com.mobile.enhancedthreadpool.threadpool;

public abstract class PoolTag {

    private String mTag;

    private String mDetail;

    public PoolTag(String tag) {
        this.mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public String getmDetail() {
        return mDetail;
    }

    public void setmDetail(String mDetail) {
        this.mDetail = mDetail;
    }
}
