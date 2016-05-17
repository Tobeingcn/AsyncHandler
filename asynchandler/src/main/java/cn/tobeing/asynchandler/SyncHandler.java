package cn.tobeing.asynchandler;

import android.os.Handler;
import android.os.Message;

/**
 * Created by sunzheng on 16/4/21.
 */
public class SyncHandler extends Handler {

    private Handler mHandler;

    public SyncHandler(Handler handler) {
        super(handler.getLooper());
        mHandler=handler;
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.obj==null){
            //default
            mHandler.handleMessage(msg);
        }else if(!(msg.obj instanceof SyncObj)){
            //default
            mHandler.handleMessage(msg);
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
     * send async message
     * @param what
     * */
    public void sendAsyncEmptyMessage(int what){
        this.sendAsyncEmptyMessageDelay(what,0);
    }
    /**
     * send async message delay
     * @param delayMillis
     * @param what
     * */
    public void sendAsyncEmptyMessageDelay(int what,long delayMillis){
        this.sendMessageDelayed(obtainMessage(what),delayMillis);
    }
    /**
     * send async message at uptimeMillis
     * @param uptimeMillis
     * @param what
     * */
    public void sendAsyncMessageAtTime(int what,long uptimeMillis){
        sendAsyncMessageAtTime(obtainMessage(what),uptimeMillis);
    }
    /**
     * send sync message
     * @param msg
     * */
    public void sendAsyncMessage(Message msg){
        this.sendAsyncMessageDelay(msg,0);
    }
    /**
     * send sync message delay
     * @param delayMillis
     * @param msg
     * */
    public void sendAsyncMessageDelay(Message msg, long delayMillis){
        msg.obj=SyncObj.obtainObj(false,msg.obj);
        super.sendMessageDelayed(msg,delayMillis);
    }
    /**
     * send async message at uptimeMillis
     * @param uptimeMillis
     * @param msg
     * */
    public void sendAsyncMessageAtTime(Message msg, long uptimeMillis){
        msg.obj=SyncObj.obtainObj(false,msg.obj);
        super.sendMessageAtTime(msg,uptimeMillis);
    }

    public void removeAsyncMessage(int what){
        removeMessages(what);
        Handlers.removeAsyncMessage(what,mHandler,false);
    }
}
