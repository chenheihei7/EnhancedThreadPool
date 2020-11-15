package com.mobile.enhancedthreadpool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.mobile.enhancedthreadpool.threadpool.PoolCallable;
import com.mobile.enhancedthreadpool.threadpool.PoolRunnable;
import com.mobile.enhancedthreadpool.threadpool.ThreadPoolUtil;

import static com.mobile.enhancedthreadpool.threadpool.ThreadPoolUtil.PRIORITY_COMPUTE;
import static com.mobile.enhancedthreadpool.threadpool.ThreadPoolUtil.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    private void test() {
        ThreadPoolUtil.getInstance().execute(new PoolRunnable(TAG) {
            @Override
            public void run() {
                Log.d(TAG, "execute in threadpool high priority");
            }
        }, PRIORITY_HIGH);

        ThreadPoolUtil.getInstance().submit(new PoolCallable(TAG) {
            @Override
            public Object call() throws Exception {
                Log.d(TAG, "submit in threadpool compute priority");
                return null;
            }
        }, PRIORITY_COMPUTE);

        ThreadPoolUtil.getInstance().removeTagTask(TAG);
    }


}