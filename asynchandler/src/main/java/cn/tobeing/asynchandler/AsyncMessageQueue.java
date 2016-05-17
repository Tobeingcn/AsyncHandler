package cn.tobeing.asynchandler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunzheng on 16/4/26.
 */
public class AsyncMessageQueue {
    private static final String TAG="AsynMessageQueue";
    private static class AsynMessageQueueHolder{

        private static AsyncMessageQueue instance=new AsyncMessageQueue();
    }
    public static AsyncMessageQueue getInstance(){
        return  AsynMessageQueueHolder.instance;
    }
    private AsyncMessageQueue(){

    }

    private Object lock=new Object();

    Map<Handler,List<Message>> cacheMessage=new HashMap<>();

    Map<Handler,Map<Integer,Message>> runningMessage=new HashMap<>();
    /**
     * 返回成功说明记录成功，失败的话，说明已经有相同id的message正在运行，则message被缓存
     *
     * */
    public boolean startMessageOrCacheMessage(Message message, Handler handler){
        synchronized (lock){
            Map<Integer,Message> runningMap=runningMessage.get(handler);
            if(runningMap==null){
                runningMap=new HashMap<>();
                runningMessage.put(handler,runningMap);
                runningMap.put(message.what,message);
                Log.d(TAG,"startMessage"+message);
                return true;
            }else{
                Message msg=runningMap.get(message.what);
                if(msg==null){
                    runningMap.put(message.what,message);
                    Log.d(TAG,"startMessage"+message);
                    return true;
                }else{
                    cacheMessage(message,handler);
                    Log.d(TAG,"cacheMessage"+message);
                }
            }
        }
        return false;
    }
    public void finishAndStartCacheMessage(Message message, Handler handler){
        synchronized (lock){
            Map<Integer,Message> runningMap=runningMessage.get(handler);
            if(runningMap==null){
                return;
            }
            runningMap.remove(message.what);
            if(runningMap.size()==0){
                runningMessage.remove(handler);
            }
            Log.d(TAG,"finishMessage"+message);

            startCacheMessage(message,handler);
        }
    }
    private void startCacheMessage(Message msg, Handler handler){
        List<Message> messages=cacheMessage.get(handler);
        if(messages==null){
            return;
        }
        Message removeMessage=null;
        for (Message message:messages) {
            if (message.what == msg.what) {
                removeMessage=message;
                break;
            }
        }
        if(removeMessage!=null){
            messages.remove(removeMessage);
            if(messages.size()==0){
                cacheMessage.remove(handler);
            }
            Log.d(TAG,"restartMessage"+removeMessage);
            Handlers.handleMessage(removeMessage,handler);
        }
    }
    private  void cacheMessage(Message message, Handler handler){
        List<Message> messages=cacheMessage.get(handler);
        if(messages==null){
            messages=new LinkedList<>();
            cacheMessage.put(handler,messages);
        }
        messages.add(message);
    }

    /**
     * 是不是优点有悖设计初衷，不建议
     * */
    protected void removeAsyncMessage(int what,Handler handler){
        synchronized (lock){
            List<Message> messages=cacheMessage.get(handler);
            if(messages==null){
                return;
            }
            List<Message> removeList=new ArrayList<>();
            for (Message message:messages) {
                if (message.what == what) {
                    removeList.add(message);
                }
            }
            messages.removeAll(removeList);
            if(messages.size()==0){
                cacheMessage.remove(handler);
            }
        }
    }
}
