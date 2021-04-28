package io.magician;

import io.magician.tcp.TCPServer;
import io.magician.udp.UDPServer;


/**
 * 主类，用NIO 创建服务
 */
public class Magician {

    /**
     * 构建一个tcp服务
     * 默认采用http解码器
     * @return
     */
    public static TCPServer createTCPServer(){
        return new TCPServer();
    }

    /**
     * 构建一个udp服务
     * @return
     */
    public static UDPServer createUdpServer(){
        return new UDPServer();
    }
}
