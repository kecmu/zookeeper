package org.apache.zookeeper;

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
        String path_base = "/test_";
        byte[] data = "ininitial data".getBytes();

        try {
            conn = new ZKConnect();
            zk = conn.connect("localhost");
            for(int i=0; i<100; i++){
                String path = path_base + i;
                create(path, data);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
