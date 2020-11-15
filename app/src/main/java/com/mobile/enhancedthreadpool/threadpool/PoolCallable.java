package com.mobile.enhancedthreadpool.threadpool;


import java.util.concurrent.Callable;

public abstract class PoolCallable extends PoolTag implements Callable{
    public PoolCallable(String tag) {
        super(tag);
    }
}
