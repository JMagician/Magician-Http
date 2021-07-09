package io.magician.tcp.workers.task;

import io.magician.common.event.EventTask;
import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.workers.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行handler的任务
 */
public class HandlerTask implements EventTask {

    private Logger logger = LoggerFactory.getLogger(HandlerTask.class);

    /**
     * 解码器根据报文你解析出来的数据
     */
    private Object resultObject;

    /**
     * 配置
     */
    private TCPServerConfig tcpServerConfig;

    /**
     * 一个工作者
     * 这里的作用是 在出现异常的时候清理自己
     */
    private Worker worker;

    /**
     * 解码器
     */
    private ProtocolCodec protocolCodec;

    public HandlerTask(Object resultObject, ProtocolCodec protocolCodec, Worker worker, TCPServerConfig tcpServerConfig){
        this.resultObject = resultObject;
        this.protocolCodec = protocolCodec;
        this.tcpServerConfig = tcpServerConfig;
        this.worker = worker;
    }

    /**
     * 执行handler任务
     * @throws Exception
     */
    @Override
    public void run() throws Exception {
        try {
            protocolCodec.handler(resultObject, tcpServerConfig);
        } catch (Exception e){
            logger.error("执行handler任务出现异常", e);
            if(worker != null){
                worker.destroy();
            }
        }
    }
}
