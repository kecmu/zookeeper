package org.apache.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * Created by wangke on 7/8/17.
 */
public class ZKDemoConnect {
    private ZooKeeper zoo;
    final CountDownLatch connected = new CountDownLatch(1);

    public ZooKeeper connect(String host) throws IOException, InterruptedException {
        zoo = new ZooKeeper(host, 5000, new Watcher() {
           public void process(WatchedEvent we) {
               if(we.getState()==KeeperState.SyncConnected){
                   connected.countDown();
               }
           }
        });

        connected.await();
        return zoo;
    }

    public void close() throws InterruptedException {
        zoo.close();
    }
}
