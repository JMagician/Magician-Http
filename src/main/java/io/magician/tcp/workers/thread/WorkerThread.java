package io.magician.tcp.workers.thread;

import io.magician.common.constant.StatusEnums;
import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.codec.ProtocolCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行请求的线程
 */
public class WorkerThread extends Thread {

    private Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    private Worker worker;

    public WorkerThread(Worker worker) {
        this.worker = worker;
    }

    /**
     * 轮询执行队列里的任务
     */
    @Override
    public void run() {
        try{
            /* 获取协议解析器 */
            ProtocolCodec protocolCodec = TCPServerConfig.getProtocolCodec();
            if(protocolCodec == null){
                return;
            }

            /* 解析数据包 */
            Object obj = protocolCodec.codecData(worker);
            if(obj == null){
                return;
            }

            /* 对于已经读完整的数据，传入handler执行业务逻辑 */
            protocolCodec.handler(obj);
        } catch (Exception e){
            logger.error("执行读数据线程队列出现异常", e);
        } finally {
            /* 设置成WAIT状态，允许下次执行 */
            if(worker != null){
                worker.setStatusEnums(StatusEnums.WAIT);
            }
        }
    }
}
