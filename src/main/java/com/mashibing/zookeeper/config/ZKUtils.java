package com.mashibing.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {

    private static ZooKeeper zk;
    private static String address="192.168.65.10:2181,192.168.65.20:2181,192.168.65.30:2181,192.168.65.40:2181,192.168.65.50:2181/testLock";
    private static DefaultWatch watch= new DefaultWatch();
    private static CountDownLatch init=new CountDownLatch(1);
    public static ZooKeeper getZK(){
        try {
            zk=new ZooKeeper(address, 1000, watch);
            watch.setCc(init);
            init.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }









}
