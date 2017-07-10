package org.apache.zookeeper;

import java.util.Random;
import java.util.LinkedList;

import org.apache.zookeeper.AsyncCallback.StringCallback;
/**
 * Created by wangke on 7/8/17.
 */
public class ZKCreate implements StringCallback {
    private static ZooKeeper zk;
    private static ZKConnect conn;
    private static LinkedList<Integer> results = new LinkedList<Integer>();

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public static void main(String[] args) {
        if(args.length!=1){
            System.out.println("usage: ./bin/javaCli.sh length");
            System.exit(0);
        }
        String path_base = "/test_";
        // String random_string = generateString("0123456789qwertyuiop[]asdfghjkl;~!@#$%^&*zxcvbnm,.", Integer.valueOf(args[0]));
        String simple_string = "helloworld";
        // byte[] long_data = random_string.getBytes();
        byte[] simple_data = simple_string.getBytes();

        try {
            ZKCreate create_obj = new ZKCreate();
            conn = new ZKConnect();
            zk = conn.connect("localhost");
            // create_obj.create(path_base, long_data);
            for(int i=0; i<200; i++) {
                // create_obj.create(path_base+i, simple_data);
                delete(path_base+i);
            }
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
        }
    }

    public static void delete(String path) throws KeeperException, InterruptedException {
        zk.delete(path, -1);
    }

}
