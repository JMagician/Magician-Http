package io.magician;

import io.magician.tcp.http.server.HttpServerCreate;
import io.magician.udp.server.UDPServerCreate;


/**
 * 服务，采用AIO
 */
public class Magician {

    /**
     * 构建一个http服务
     * @return
     */
    public static HttpServerCreate createHttpServer(){
        return new HttpServerCreate();
    }

    /**
     * 构建一个udp服务
     * @return
     */
    public static UDPServerCreate createUdpServer(){
        return new UDPServerCreate();
    }
}
