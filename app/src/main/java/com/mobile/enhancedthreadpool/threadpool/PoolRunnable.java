package com.mobile.enhancedthreadpool.threadpool;

public abstract class PoolRunnable extends PoolTag implements Runnable{

    public PoolRunnable(String tag) {
        super(tag);
    }

}
