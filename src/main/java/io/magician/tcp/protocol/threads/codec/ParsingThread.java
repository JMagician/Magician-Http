package io.magician.tcp.protocol.threads.codec;

import io.magician.common.threadpool.ThreadPoolManagerFactory;
import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.protocol.threads.handler.ExecuteModel;
import io.magician.tcp.protocol.model.ProtocolDataModel;
import io.magician.tcp.protocol.codec.ProtocolCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 执行请求的线程
 */
public class ParsingThread extends Thread {

    private Logger logger = LoggerFactory.getLogger(ParsingThread.class);

    private LinkedBlockingDeque<ProtocolDataModel> blockingDeque;

    public ParsingThread(LinkedBlockingDeque<ProtocolDataModel> blockingDeque) {
        this.blockingDeque = blockingDeque;
    }

    /**
     * 轮询执行队列里的任务
     */
    @Override
    public void run() {
        while (true){
            try{
                ProtocolDataModel protocolDataModel = blockingDeque.take();

                /* 获取协议解析器 */
                ProtocolCodec protocolCodec = TCPServerConfig.getProtocolCodec();
                if(protocolCodec == null){
                    return;
                }

                /* 解析数据包 */
                Object obj = protocolCodec.parsingData(protocolDataModel);
                if(obj == null){
                    return;
                }

                /* 对于已经读完整的数据，丢入业务线程池进行处理 */
                ExecuteModel executeModel = new ExecuteModel();
                executeModel.setData(obj);
                executeModel.setProtocolCodec(protocolCodec);

                ThreadPoolManagerFactory
                        .getThreadPoolManager(ThreadPoolManagerFactory.TCP_EXECUTE)
                        .addTask(executeModel);
            } catch (Exception e){
                logger.error("执行读数据线程队列出现异常", e);
            }
        }
    }
}
