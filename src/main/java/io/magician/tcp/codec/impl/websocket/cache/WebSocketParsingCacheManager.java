package io.magician.tcp.codec.impl.websocket.cache;

import io.magician.tcp.attach.AttachmentModel;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.workers.Worker;

public class WebSocketParsingCacheManager {

    /**
     * 获取附件
     * @param worker
     * @return
     */
    public static WebSocketParsingModel getWebSocketParsingCacheModel(Worker worker, AttachmentModel attachmentModel){
        WebSocketParsingModel webSocketParsingModel = worker.getAttach(WebSocketParsingModel.class);
        if(webSocketParsingModel == null){
            webSocketParsingModel = new WebSocketParsingModel();

            WebSocketExchange webSocketExchange = new WebSocketExchange();
            if (attachmentModel != null) {
                webSocketExchange.setWebSocketSession(attachmentModel.getWebSocketSession());
            }

            webSocketParsingModel.setWebSocketExchange(webSocketExchange);
            worker.setAttach(webSocketParsingModel);
        }

        return webSocketParsingModel;
    }

    /**
     * 清理附件
     * @param worker
     */
    public static void clear(Worker worker){
        worker.setAttach(null);
    }
}
