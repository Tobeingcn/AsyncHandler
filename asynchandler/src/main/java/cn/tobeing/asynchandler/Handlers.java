package cn.tobeing.asynchandler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sunzheng on 16/4/21.
 */
public class Handlers {

    private static ExecutorService service= Executors.newCachedThreadPool();
    private static Map<String,WeakReference<SyncHandler>> syncMap=new HashMap<>();

    private static Map<String,WeakReference<AsyncHandler>> asyncMap=new HashMap<>();



    private static Object syncLock=new Object();

    private static Object asyncLock=new Object();
    private static String getCacheKey(Handler handler, boolean isSync){
        return handler.getClass().getName()+"#"+handler.hashCode()+"#"+(isSync?"isSync":"isAsync");
    }
    /**
     * async this handle,and return a handl that send message async
     */
    public static AsyncHandler async(Handler handler) {
        synchronized (asyncLock) {
            AsyncHandler handler1=getCacheAsyncHandler(handler);
            if(handler1==null){
                handler1=new AsyncHandler(handler);
                asyncMap.put(getCacheKey(handler,false),new WeakReference<AsyncHandler>(handler1));
                Log.d("handlers","new async handler");
            }else{
                Log.d("handlers","get async handler from cache");
            }
            return handler1;
        }
    }
    /**
     * sync this handle,and return a handl that send message async
     */
    public static SyncHandler sync(Handler handler) {
        synchronized (syncLock) {
            SyncHandler handler1=getCacheSyncHandler(handler);
            if(handler1==null){
                handler1=new SyncHandler(handler);
                syncMap.put(getCacheKey(handler,true),new WeakReference<SyncHandler>(handler1));
                Log.d("handlers","new sync handler");
            }else{
                Log.d("handlers","get sync handler from cache");
            }
            return handler1;
        }
    }

    private static SyncHandler getCacheSyncHandler(Handler handler){
        synchronized (syncLock) {
            String cacheKey=getCacheKey(handler, true);
            WeakReference<SyncHandler> weakReference = syncMap.get(cacheKey);
            if (weakReference == null) {
                return null;
            } else {
                SyncHandler handler1 = weakReference.get();
                if (handler == null) {
                    syncMap.remove(cacheKey);
                }
                return handler1;
            }
        }
    }
    private static AsyncHandler getCacheAsyncHandler(Handler handler){
        synchronized (asyncLock) {
            String cacheKey=getCacheKey(handler, false);
            WeakReference<AsyncHandler> weakReference = asyncMap.get(cacheKey);
            if (weakReference == null) {
                return null;
            } else {
                AsyncHandler handler1 = weakReference.get();
                if (handler == null) {
                    asyncMap.remove(cacheKey);
                }
                return handler1;
            }
        }
    }
    protected static void handleMessage(final Message msg, final Handler hanlder){
        final Message newMessage=new Message();
        newMessage.copyFrom(msg);
        service.execute(new Runnable() {
            @Override
            public void run() {
                if(AsyncMessageQueue.getInstance().startMessageOrCacheMessage(newMessage,hanlder)) {
                    hanlder.handleMessage(newMessage);
                    AsyncMessageQueue.getInstance().finishAndStartCacheMessage(newMessage,hanlder);
                }
            }
        });
    }

    /**
     * 
     * 删除异步消息，包括本身message队列中的消息
     * */
    public static void removeAsyncMessage(int what,Handler handler){
        removeAsyncMessage(what,handler,true);
    }

    protected static void removeAsyncMessage(int what, Handler handler, boolean removeInner){
        if(removeInner) {
            SyncHandler handler1=getCacheSyncHandler(handler);
            if(handler1!=null) {
                Log.d("handlers","getCacheSyncHandler "+getCacheKey(handler,true)+"."+handler1.hashCode());
                handler1.removeMessages(what);
            }
            AsyncHandler handler2=getCacheAsyncHandler(handler);
            if(handler2!=null){
                Log.d("handlers","getCacheAsyncHandler "+getCacheKey(handler,false)+"."+handler2.hashCode());
                handler2.removeMessages(what);
            }
            handler.removeMessages(what);
        }
        AsyncMessageQueue.getInstance().removeAsyncMessage(what,handler);
    }
}
