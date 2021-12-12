package io.magician;


import io.magician.network.HttpServer;

/**
 * 主类，创建服务
 */
public class Magician {

    /**
     * 创建http
     * @return
     * @throws Exception
     */
    public static HttpServer createHttp() throws Exception {
        return new HttpServer();
    }
}
