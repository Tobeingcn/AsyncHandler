package cn.tobeing.test;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import cn.tobeing.asynchandler.Handlers;

public class MainActivity extends AppCompatActivity {
    Handler handler;
    private AtomicInteger mCount=new AtomicInteger(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HandlerThread th = new HandlerThread("TestSyncFragment");
        th.start();
        handler = new Handler(th.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0: {
                        int count = mCount.getAndIncrement();
                        Log.d("suntest", "===========================count=" + count + ",thread=" + Thread.currentThread().getName());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("suntest", "##############what 0,count=" + count + ",thread=" + Thread.currentThread().getName());
                    }
                    break;
                    case 1: {
                        int count = mCount.getAndIncrement();
                        Log.d("suntest", "===========================count=" + count + ",thread=" + Thread.currentThread().getName());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("suntest", "##############what 0,count=" + count + ",thread=" + Thread.currentThread().getName());
                    }
                    break;

                }
            }
        };
        handler.sendEmptyMessage(0);
        handler.sendEmptyMessageDelayed(0, 100);
        Handlers.async(handler).sendEmptyMessage(0);
        Handlers.async(handler).sendEmptyMessage(0);
        Handlers.async(handler).sendEmptyMessageDelayed(0, 100);
        Handlers.async(handler).removeAsyncMessage(0);
        Handlers.async(handler).sendEmptyMessage(0);
    }
}
