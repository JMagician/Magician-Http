package com.mars.server.http.handler;

import com.mars.server.http.request.MartianHttpExchange;

/**
 * 自定义联络器接口
 */
public interface MartianServerHandler {

    void request(MartianHttpExchange martianHttpExchange);
}
