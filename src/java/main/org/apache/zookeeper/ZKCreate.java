package org.apache.zookeeper;

import java.util.Random;
/**
 * Created by wangke on 7/8/17.
 */
public class ZKCreate {
    private static ZooKeeper zk;
    private static ZKConnect conn;

    public static void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public static void main(String[] args) {
        String path_base = "/test_long";
        String random_string = generateString("0123456789qwertyuiop[]asdfghjkl;~!@#$%^&*zxcvbnm,.", 10);
        String simple_string = "h";
        byte[] long_data = random_string.getBytes();
        byte[] simple_data = simple_string.getBytes();

        try {
            conn = new ZKConnect();
            zk = conn.connect("localhost");
            create(path_base, long_data);
            create("/test_short", simple_data);
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


}
