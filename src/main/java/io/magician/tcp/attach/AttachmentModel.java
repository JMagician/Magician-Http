package io.magician.tcp.attach;

import io.magician.tcp.codec.impl.websocket.connection.WebSocketSession;
import io.magician.common.event.EventRunner;
import io.magician.tcp.workers.Worker;

/**
 * 附件实体
 */
public class AttachmentModel {

    /**
     * 工作者
     * 每个连接对应一个，用来保存数据
     */
    private Worker worker = new Worker();

    /**
     * websocket会话
     * 只有websocket连接 这个属性才有数据
     */
    private WebSocketSession webSocketSession;

    /**
     * 这个连接绑定的事件执行器
     */
    private EventRunner eventRunner;

    /**
     * 扩展字段
     * 如果后期添加新的解码器，可能需要保存其他附件
     * 所以预留这个字段
     */
    private Object expand;

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public EventRunner getEventRunner() {
        return eventRunner;
    }

    public void setEventRunner(EventRunner eventRunner) {
        this.eventRunner = eventRunner;
    }

    public Object getExpand() {
        return expand;
    }

    public void setExpand(Object expand) {
        this.expand = expand;
    }
}
