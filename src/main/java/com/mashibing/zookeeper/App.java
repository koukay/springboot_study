package com.mashibing.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World");

        //zk是有session概念的,没有连接池的概念的
        //watch观察回调分为两类
        //watch的注册只发生在读类型调用,get ,exist
        //第一类,new zk的时候,传入的watch,这个watch是session级别的,跟path与node没有关系
        CountDownLatch cd = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper("192.168.65.10:2181,192.168.65.20:2181,192.168.65.30:2181,192.168.65.40:2181,192.168.65.50:2181"
                , 3000,
                new Watcher() {
                    /**
                     * watch的回调方法
                     * @param event
                     */
                    @Override
                    public void process(WatchedEvent event) {
                        Event.KeeperState state = event.getState();
                        Event.EventType type = event.getType();
                        String path = event.getPath();
                        System.out.println("==========new zk watch"+event.toString());
                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                System.out.println("==================>connected");
                                cd.countDown();
                                break;
                            case AuthFailed:
                                break;
                            case ConnectedReadOnly:
                                break;
                            case SaslAuthenticated:
                                break;
                            case Expired:
                                break;
                            case Closed:
                                break;
                        }
                        switch (type) {
                            case None:
                                break;
                            case NodeCreated:
                                break;
                            case NodeDeleted:
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
                });
        cd.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("===========ing======================");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("===========ed======================");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }
        String pathName = zk.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Stat stat = new Stat();
        byte[] node = zk.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("===========getData Watch"+event.toString());
                try {
                    //true Default Watch被重新注册 (new zk的那个watch)
                    zk.getData("/ooxx", true, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println("=================="+new String(node));
        //触发回调
        Stat stat1 = zk.setData("/ooxx", "newData".getBytes(), 0);
        //第二次还会触发吗
        Stat stat2 = zk.setData("/ooxx", "newData01".getBytes(), stat1.getVersion());
        System.out.println("----------------async start --------------");
        zk.getData("/ooxx", false, new AsyncCallback.DataCallback(){

            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("----------------async over --------------");
                System.out.println("----------"+ctx.toString());
                System.out.println("----------"+new String(data));
            }
        },"123");
        System.out.println("----------------async call back --------------");
        Thread.sleep(2222222);

    }
}
