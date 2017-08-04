package org.apache.zookeeper;

import java.util.Random;
import java.util.LinkedList;

import org.apache.zookeeper.AsyncCallback.StringCallback;
/**
 * Created by wangke on 7/8/17.
 */
public class ZKDemo extends Thread implements StringCallback {
    private ZooKeeper zk;
    private ZKDemoConnect conn;
    private LinkedList<Integer> results = new LinkedList<Integer>();
    private int requests_sent = 0;
    private int total_requests_per_worker = 0;
    private String path_base;
    private byte[] simple_data;
    private String action;
    private String host;

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public byte[] get(String path) throws KeeperException, InterruptedException {
        return zk.getData(path, false, null);
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        this.zk.delete(path, -1);
    }


    public ZKDemo(int total_per_worker, String path, String action, String host){
        this.total_requests_per_worker = total_per_worker;
        this.path_base = path;
        this.action = action;
        this.simple_data = "hello_world".getBytes();
        this.host = host;
    }

    public void run() {
        /*if(args.length!=5){
            System.out.println("usage: ./bin/javaCli.sh server_ip option num rate zcode_base_name");
            System.out.println("hello, excuse me?");
            System.exit(0);
        }*/
        long average_delay = 0;
        try {
            this.conn = new ZKDemoConnect();
            this.zk = this.conn.connect(host);
            if(this.action.startsWith("d")){
                for(int i=0; i<this.total_requests_per_worker; i++) {
                    delete(path_base+i);
                }
            }
            else if(this.action.startsWith("g")) {
                for(int i=0; i<this.total_requests_per_worker; i++) {
                    long start = System.nanoTime();
                    this.get(path_base+requests_sent);
                    long end = System.nanoTime();
                    requests_sent += 1;
                    long duration = end - start;
                    if(requests_sent >1000)
                        average_delay += duration;
                }
            }
            else{
                for(int i=0; i<this.total_requests_per_worker; i++) {
                    long start = System.nanoTime();
                    this.create(path_base+requests_sent, simple_data);
                    requests_sent += 1;
                    long end = System.nanoTime();
                    long duration = end - start;
                    if(requests_sent >1000)
                        average_delay += duration;
                }
            }
            double ave = ((double)(average_delay)) / (this.total_requests_per_worker-1000);
            System.out.println("average delay: "+ave);
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void processResult(int rc, String path, Object ctx, String name) {
        synchronized(ctx) {
            ((LinkedList<Integer>)ctx).add(rc);
            ctx.notifyAll();
        }
    }
}
