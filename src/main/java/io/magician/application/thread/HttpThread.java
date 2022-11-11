package io.magician.application.thread;

import io.magician.application.request.MagicianRequest;
import io.magician.application.request.MagicianResponse;
import io.magician.application.request.WebSocketSession;
import io.magician.common.cache.MagicianHandlerCache;
import io.magician.common.constant.WebSocketConstant;
import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.handler.WebSocketBaseHandler;
import io.magician.network.processing.exchange.HttpExchange;
import io.magician.network.processing.exchange.WebSocketExchange;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http business thread
 */
public class HttpThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(HttpThread.class);

    private HttpExchange exchange;
    MagicianRequest request;
    MagicianResponse response;

    public HttpThread(HttpExchange exchange){
        this.exchange = exchange;
    }

    @Override
    public void run() {
        try {
            request = new MagicianRequest(exchange, response);
            response = new MagicianResponse(exchange);

            String path = getPath(request);

            if (isWebSocket(request)) {
                // If it is websocket, upgrade the protocol
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request.getHttpExchange().getFullHttpRequest()), null, true, Integer.MAX_VALUE);
                WebSocketServerHandshaker webSocketServerHandshaker = wsFactory.newHandshaker(request.getHttpExchange().getFullHttpRequest());
                if (webSocketServerHandshaker == null) {
                    WebSocketServerHandshakerFactory
                            .sendUnsupportedVersionResponse(request.getHttpExchange().getChannelHandlerContext().channel());
                } else {
                    webSocketServerHandshaker.handshake(request.getHttpExchange().getChannelHandlerContext().channel(), request.getHttpExchange().getFullHttpRequest());
                }
                String channelId = request.getHttpExchange().getChannelHandlerContext().channel().id().asLongText();

                // Create a session object
                WebSocketSession webSocketSession = new WebSocketSession();
                webSocketSession.setChannelHandlerContext(exchange.getChannelHandlerContext());
                webSocketSession.setId(channelId);

                // Find the corresponding handler
                WebSocketBaseHandler webSocketBaseHandler = MagicianHandlerCache.getWebSocketHandler(path);
                if (webSocketBaseHandler == null) {
                    logger.error("There is no corresponding handler: [" + path + "]");
                    return;
                }
                // Bind the session and handler to this connection, and all subsequent messages are processed through this handler
                WebSocketExchange webSocketExchange = new WebSocketExchange();
                webSocketExchange.setWebSocketBaseHandler(webSocketBaseHandler);
                webSocketExchange.setWebSocketServerHandshaker(webSocketServerHandshaker);
                webSocketExchange.setWebSocketSession(webSocketSession);
                MagicianHandlerCache.addWebSocketSessionMap(channelId, webSocketExchange);

                webSocketBaseHandler.onOpen(webSocketSession);
                return;
            }

            // If it is an http request, execute the handler directly
            HttpBaseHandler httpBaseHandler = MagicianHandlerCache.getHttpHandler(path);
            if (httpBaseHandler != null) {
                httpBaseHandler.request(request, response);
                return;
            }

            httpBaseHandler = MagicianHandlerCache.getHttpHandler("/");
            if (httpBaseHandler != null) {
                httpBaseHandler.request(request, response);
                return;
            }

            response.sendErrorMsg(404, "There is no corresponding handler:[" + path + "]");
        } catch (Exception e) {
            logger.error("An exception occurred while processing the request", e);
            exchange.getChannelHandlerContext().close();
        }
    }

    /**
     * refuse to execute
     */
    public void rejectedExecution() throws Exception {
        response.sendErrorMsg(403, "The thread pool is full, the server refused to execute the request, please try again later");
    }

    /**
     * get request path
     * @param request
     * @return
     */
    private static String getPath(MagicianRequest request){
        String url = request.getUrl();
        int lastIndex = url.lastIndexOf("?");

        if (lastIndex > -1) {
            url = url.substring(0, lastIndex);
        }
        if (url.startsWith("/") == false) {
            url = "/" + url;
        }
        return url;
    }

    /**
     * Is it a webSocket
     * @param request
     * @return
     */
    private static boolean isWebSocket(MagicianRequest request){
        if(!HttpMethod.GET.equals(request.getMethod())){
            return false;
        }

        HttpHeaders httpHeaders = request.getRequestHeaders();
        String upgrade = httpHeaders.get(WebSocketConstant.UPGRADE);
        String connection = httpHeaders.get(WebSocketConstant.CONNECTION);
        String swKey = httpHeaders.get(WebSocketConstant.SEC_WEBSOCKET_KEY);
        if(upgrade == null || connection == null || swKey == null){
            return false;
        }

        if(connection.toUpperCase().indexOf(WebSocketConstant.UPGRADE.toUpperCase()) == -1){
            return false;
        }

        return true;
    }

    /**
     * Get the WebSocketLocation used to establish the connection
     * @param req
     * @return
     */
    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + req.uri();
        return "ws://" + location;
    }
}
