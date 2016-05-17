package cn.tobeing.asynchandler;

import android.os.Handler;
import android.os.Message;

/**
 * send default message async
 *
 * Created by sunzheng on 16/4/21.
 */
public class AsyncHandler extends Handler {

    private Handler mHandler;

    public AsyncHandler(Handler handler){
        super(handler.getLooper());
        mHandler=handler;
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.obj==null){
            Handlers.handleMessage(msg,mHandler);
        }else if(!(msg.obj instanceof SyncObj)){
            Handlers.handleMessage(msg,mHandler);
        }else {
            SyncObj obj= (SyncObj) msg.obj;
            msg.obj=obj.getObject();
            if(obj.isSync()){
                mHandler.handleMessage(msg);
            }else{
                Handlers.handleMessage(msg,mHandler);
            }
            SyncObj.recyle(obj);
        }
    }
    /**
     * send sync message
     * @param what
     * */
    public void sendSyncEmptyMessage(int what){
        this.sendSyncEmptyMessageDelay(what,0);
    }
    /**
     * send sync message delay
     * @param delayMillis
     * @param what
     * */
    public void sendSyncEmptyMessageDelay(int what,long delayMillis){
        this.sendMessageDelayed(obtainMessage(what),delayMillis);
    }
    /**
     * send sync message at uptimeMillis
     * @param uptimeMillis
     * @param what
     * */
    public void sendSyncMessageAtTime(int what,long uptimeMillis){
        sendSyncMessageAtTime(obtainMessage(what),uptimeMillis);
    }
    /**
     * send sync message
     * @param msg
     * */
    public void sendSyncMessage(Message msg){
        this.sendSyncMessageDelay(msg,0);
    }
    /**
     * send sync message delay
     * @param delayMillis
     * @param msg
     * */
    public void sendSyncMessageDelay(Message msg, long delayMillis){
        msg.obj=SyncObj.obtainObj(true,msg.obj);
        super.sendMessageDelayed(msg,delayMillis);
    }

    public void sendSyncMessageAtTime(Message msg, long uptimeMillis){
        msg.obj=SyncObj.obtainObj(true,msg.obj);
        super.sendMessageAtTime(msg,uptimeMillis);
    }

    public void removeAsyncMessage(int what){
        removeMessages(what);
        Handlers.removeAsyncMessage(what,mHandler,false);
    }

}
