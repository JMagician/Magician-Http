package io.magician.tcp.http.handler;

import io.magician.tcp.http.request.MagicianRequest;

/**
 * 自定义联络器接口
 */
public interface MagicianHandler {

    void request(MagicianRequest request);
}
