package io.magician;


import io.magician.network.HttpServer;

/**
 * Main class, create service
 */
public class Magician {

    /**
     * create http
     * @return
     * @throws Exception
     */
    public static HttpServer createHttp() throws Exception {
        return new HttpServer();
    }
}
