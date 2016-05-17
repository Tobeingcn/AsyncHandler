package cn.tobeing.asynchandler;

import android.util.Log;

/**
 * Created by sunzheng on 16/5/17.
 */
public class MyLog {
    private static final boolean DEBUG=true;
    public static void d(String tag,String message){
        if(DEBUG){
            Log.d(tag, message);
        }
    }
}
