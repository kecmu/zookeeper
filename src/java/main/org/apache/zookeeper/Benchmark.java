package org.apache.zookeeper;

/**
 * Created by wangke on 8/3/17.
 */
public class Benchmark {
    public static void main(String[] args){
        if(args.length!=5){
            System.out.println("usage: ./bin/javaCli.sh server_ip option num rate zcode_base_name");
            System.out.println("hello, excuse me?");
            System.exit(0);
        }
        for(int i=0; i<Integer.valueOf(args[3]); i++){
            ZKDemo zkdemo = new ZKDemo(Integer.valueOf(args[2]), '/'+i+args[4], args[1], args[0]);
            zkdemo.start();
        }
    }
}
