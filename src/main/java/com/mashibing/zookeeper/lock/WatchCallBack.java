package com.mashibing.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher , AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    ZooKeeper zk;
    String threadName;
    CountDownLatch cc = new CountDownLatch(1);

    public CountDownLatch getCc() {
        return cc;
    }

    public void setCc(CountDownLatch cc) {
        this.cc = cc;
    }

    String pathName;
    public ZooKeeper getZk() {
        return zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void tryLock(){
            try {
                System.out.println(threadName+"  -----------------get lock---------------");
                zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL_SEQUENTIAL,this,"ewf");
                cc.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void unLock(){

        try {
            zk.delete(pathName, -1);
            System.out.println(pathName+"  ------------ over lock ---------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void process(WatchedEvent event) {
        //如果第一个哥们,那个锁释放了,其实只有第二个收到回调事件
        //如果不是第一个哥们,某一个挂了,也能造成他后面的收到这个通知,从而让它后面那个哥们去监控watch 挂掉哥们前面的
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "sfdsf");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name != null){
            System.out.println(threadName+"================create node===================-----------: "+name);
            pathName=name;
            zk.getChildren("/", false, this, "sfdsf");
        }
    }

    /**
     * getchildren call back
     * @param rc
     * @param path
     * @param ctx
     * @param children
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        //进入这个方法说明一定可以看到自己前面的
        /*System.out.println(threadName+"================locks===================-----------: ");
        for (String child : children) {
            System.out.println(child);
        }*/
        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));

        //是不是第一个
        if (i==0) {
            //如果是,开始countdown
            System.out.println(threadName+"  i am first===============================");
            try {
                zk.setData("/", threadName.getBytes(), -1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cc.countDown();
        }else {
            //如果不是
            zk.exists("/"+children.get(i-1), this, this, "wred");
        }



    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }
}
