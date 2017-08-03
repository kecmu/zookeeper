package org.apache.zookeeper;

import java.util.Random;
import java.util.LinkedList;

import org.apache.zookeeper.AsyncCallback.StringCallback;
/**
 * Created by wangke on 7/8/17.
 */
public class ZKDemo implements StringCallback {
    private static ZooKeeper zk;
    private static ZKDemoConnect conn;
    private static LinkedList<Integer> results = new LinkedList<Integer>();
    private static int requests_sent = 0;
    private static int total_requests = 0;
    private static int MAX_REQUEST_ON_FLY = 0;
    private static String path_base;
    private static byte[] simple_data;

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public static void main(String[] args) {
        if(args.length!=5){
            System.out.println("usage: ./bin/javaCli.sh server_ip option num rate zcode_base_name");
            System.out.println("hello, excuse me?");
            System.exit(0);
        }
        path_base = args[4];
        // String random_string = generateString("0123456789qwertyuiop[]asdfghjkl;~!@#$%^&*zxcvbnm,.", Integer.valueOf(args[0]));
        String simple_string = "helloworld";
        // byte[] long_data = random_string.getBytes();
        simple_data = simple_string.getBytes();
        total_requests = Integer.valueOf(args[2]);
        MAX_REQUEST_ON_FLY = Integer.valueOf(args[3]);
        long average_delay = 0;

        try {
            ZKDemo create_obj = new ZKDemo();
            conn = new ZKDemoConnect();
            zk = conn.connect(args[0]);
            // create_obj.create(path_base, long_data);
            if(args[1].startsWith("d")){
                for(int i=0; i<Integer.valueOf(args[2]); i++) {
                    delete(path_base+i);
                }
            }
            else{
                for(int i=0; i<ZKDemo.MAX_REQUEST_ON_FLY; i++) {
                    long start = System.currentTimeMillis();
                    create_obj.create(path_base+requests_sent, simple_data);
                    long end = System.currentTimeMillis();
                    long duration = end - start;
                    average_delay += duration;
                    System.out.println(duration);
                }
            }
            average_delay = average_delay / MAX_REQUEST_ON_FLY;
            System.out.println("average delay: "+(int)(average_delay));
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String generateString(String characters, int length)
    {
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    @SuppressWarnings("unchecked")
    public void processResult(int rc, String path, Object ctx, String name) {
        synchronized(ctx) {
            ((LinkedList<Integer>)ctx).add(rc);
            ctx.notifyAll();
            /*try {
                while(requests_sent<total_requests){
                    zk.create(path_base+requests_sent, simple_data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    requests_sent += 1;
                }
            }catch(Exception e) {
                System.out.println(e.getMessage());
            }*/
        }
    }

    public static void delete(String path) throws KeeperException, InterruptedException {
        zk.delete(path, -1);
    }

}
