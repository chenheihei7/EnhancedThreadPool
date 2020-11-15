package com.mobile.enhancedthreadpool.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EnhanceThreadPoolExecutor extends ThreadPoolExecutor {

    private ConcurrentHashMap<String, String> mConcurrentHashMap;

    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (!ThreadPoolUtil.getInstance().sDebugable) {
            return;
        }
        if (r instanceof PoolTag) {
            if (null == mConcurrentHashMap) {
                mConcurrentHashMap = new ConcurrentHashMap();
            }
            PoolTag poolTag = (PoolTag)r;
            String detail = poolTag.getmDetail();
            String tag = poolTag.getTag();
            String value = String.valueOf(System.currentTimeMillis());
            mConcurrentHashMap.put(tag + detail, value);

        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (null != t) {
            System.out.println(t);
        }
        if (!ThreadPoolUtil.getInstance().sDebugable) {
            return;
        }
        if (null == mConcurrentHashMap) {
            return;
        }
        if (r instanceof PoolTag) {
            PoolTag poolTag = (PoolTag)r;
            String detail = poolTag.getmDetail();
            String tag = poolTag.getTag();
            String current = String.valueOf(System.currentTimeMillis());
            String value = mConcurrentHashMap.get(tag + detail);
            long cost = Long.valueOf(current) - Long.valueOf(value);
            mConcurrentHashMap.put(tag + detail, value + "_" + current + "_" + cost);
        }

    }

    @Override
    protected void terminated() {
        super.terminated();
    }

}
