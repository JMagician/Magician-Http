package com.mars.server.tcp.http.handler.ext;

import com.mars.server.tcp.http.handler.MartianServerHandler;
import com.mars.server.tcp.http.request.MartianHttpRequest;

/**
 * 更加简单的联络器
 * HttpExchange的扩展，不仅分好了数据，还将参数都解析出来，并分类存放好了
 */
public interface HttpRequestHandler extends MartianServerHandler<MartianHttpRequest> {

    void request(MartianHttpRequest martianHttpRequest);
}
