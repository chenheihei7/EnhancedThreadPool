package com.mobile.enhancedthreadpool.threadpool;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

    private static class SingleHolder{
        private static ThreadPoolUtil instance = new ThreadPoolUtil();
    }

    public static ThreadPoolUtil getInstance() {
        return SingleHolder.instance;
    }

    //优先级高任务
    public static final String PRIORITY_HIGH = "high";
    //计算型任务
    public static final String PRIORITY_COMPUTE = "compute";
    //IO型任务
    public static final String PRIORITY_IO = "io";

    private EnhanceThreadPoolExecutor mHighThreadPoolExecutor;

    private EnhanceThreadPoolExecutor mComputeThreadPoolExecutor;

    private EnhanceThreadPoolExecutor mIOThreadPoolExecutor;

    private int mIOThreadCoreSize, mComputeThreadCoreSize;

    private SynchronousQueue mHighBlockingQueue = new SynchronousQueue();

    private LinkedBlockingQueue mIOBlockingQueue = new LinkedBlockingQueue();

    private LinkedBlockingQueue mComputeBlockingQueue = new LinkedBlockingQueue();

    protected volatile boolean sDebugable = false;

    private HashSet<String> mTagSet = new HashSet<>();

    private ThreadPoolUtil() {
        init();
    }

    private void init() {
        int core = Runtime.getRuntime().availableProcessors();
        mComputeThreadCoreSize = core;
        mIOThreadCoreSize = core << 1;
    }

    private void initHighThreadPoolExecutor () {
            mHighThreadPoolExecutor = new EnhanceThreadPoolExecutor(0,
                    Integer.MAX_VALUE,
                    60L,
                    TimeUnit.SECONDS,
                    mHighBlockingQueue);
    }

    private void initComputeThreadPoolExecutor() {
        mComputeThreadPoolExecutor = new EnhanceThreadPoolExecutor(mComputeThreadCoreSize,
                0,
                0L,
                TimeUnit.SECONDS,
                mComputeBlockingQueue);
    }

    private void initIOThreadPoolExecutor() {
        mIOThreadPoolExecutor = new EnhanceThreadPoolExecutor(mIOThreadCoreSize,
                0,
                0L,
                TimeUnit.SECONDS,
                mIOBlockingQueue);
    }

    /**
     * @param runnable
     * @param priority
     */
    public void execute(PoolRunnable runnable, String priority) {
        handleRunnable(runnable);
        switch (priority) {
            case PRIORITY_HIGH:
                if (null == mHighThreadPoolExecutor) {
                    initHighThreadPoolExecutor();
                }
                mHighThreadPoolExecutor.execute(runnable);
                break;

            case PRIORITY_COMPUTE:
                if (null == mComputeThreadPoolExecutor) {
                    initComputeThreadPoolExecutor();
                }
                mComputeThreadPoolExecutor.execute(runnable);
                break;

            case PRIORITY_IO:
                if (null == mIOThreadPoolExecutor) {
                    initIOThreadPoolExecutor();
                }
                mIOThreadPoolExecutor.execute(runnable);
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    public Future submit(PoolCallable callable, String priority) {
        handleRunnable(callable);
        switch (priority) {
            case PRIORITY_HIGH:
                if (null == mHighThreadPoolExecutor) {
                    initHighThreadPoolExecutor();
                }
                return mHighThreadPoolExecutor.submit(callable);

            case PRIORITY_COMPUTE:
                if (null == mComputeThreadPoolExecutor) {
                    initComputeThreadPoolExecutor();
                }
                return mComputeThreadPoolExecutor.submit(callable);

            case PRIORITY_IO:
                if (null == mIOThreadPoolExecutor) {
                    initIOThreadPoolExecutor();
                }
                return mIOThreadPoolExecutor.submit(callable);

            default:
                throw new IllegalArgumentException();
        }
    }

    public void removeRunnable(Runnable runnable) {}

    private void handleRunnable(PoolTag poolTag) {
        if (!sDebugable) return;
        if (TextUtils.isEmpty(poolTag.getmDetail())) {
            poolTag.setmDetail(UUID.randomUUID().toString().substring(24));
        }
    }

    /**
     * @param tag
     * @param priority
     */
    public void removeTagTask(String tag, String priority) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        Iterator<PoolRunnable> iterator = null;
        switch (priority) {
            case PRIORITY_HIGH:
                iterator = mHighBlockingQueue.iterator();
                break;

            case PRIORITY_COMPUTE:
                iterator = mComputeBlockingQueue.iterator();
                break;

            case PRIORITY_IO:
                iterator = mIOBlockingQueue.iterator();
                break;

            default:
                throw new IllegalArgumentException();
        }

        while (iterator.hasNext()) {
            PoolRunnable PoolRunnable = iterator.next();
            if (PoolRunnable.getTag().equals(tag)) {
                iterator.remove();
            }
        }

    }

    /**
     * @param tag
     */
    public void removeTagTask(String tag) {
        removeTagTask(tag, PRIORITY_HIGH);
        removeTagTask(tag, PRIORITY_COMPUTE);
        removeTagTask(tag, PRIORITY_IO);
    }

    public void shutDown(String priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                if (null != mHighThreadPoolExecutor) {
                    mHighThreadPoolExecutor.shutdown();
                }
                break;

            case PRIORITY_COMPUTE:
                if (null != mComputeThreadPoolExecutor) {
                    mComputeThreadPoolExecutor.shutdown();
                }
                break;

            case PRIORITY_IO:
                if (null != mIOThreadPoolExecutor) {
                    mIOThreadPoolExecutor.shutdown();
                }
                break;

            default:
                break;
        }
    }

    public void shutDown() {
        shutDown(PRIORITY_HIGH);
        shutDown(PRIORITY_COMPUTE);
        shutDown(PRIORITY_IO);
    }

    public void setsDebugable(boolean debugable) {
        sDebugable = debugable;
    }


}
