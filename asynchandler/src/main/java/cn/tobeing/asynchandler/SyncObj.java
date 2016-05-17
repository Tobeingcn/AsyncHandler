package cn.tobeing.asynchandler;

import java.util.LinkedList;

/**
 * Created by sunzheng on 16/4/21.
 */
public class SyncObj {
    private static LinkedList<SyncObj> syncObjs=new LinkedList<>();

    private boolean isSync;

    private Object object;

    public SyncObj(Object obj){
        this(true,obj);
    }

    public SyncObj(Boolean isSync, Object obj){
        this.isSync=isSync;
        this.object=obj;
    }
    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
    public static SyncObj obtainObj(Boolean isSync, Object obj) {
        synchronized (syncObjs) {
            if (syncObjs.size() > 0) {
                SyncObj syncObj = syncObjs.remove();
                syncObj.setSync(isSync);
                syncObj.setObject(obj);
                return syncObj;
            } else {
                return new SyncObj(isSync, obj);
            }
        }
    }

    public static void recyle(SyncObj syncObj){
        synchronized (syncObjs){
            syncObjs.add(syncObj);
        }
    }
}
