package io.magician.tcp.workers.task;

import io.magician.common.constant.StatusEnums;
import io.magician.tcp.TCPServerConfig;
import io.magician.common.event.EventTask;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.codec.ProtocolCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行请求的任务
 */
public class WorkerTask implements EventTask {

    private Logger logger = LoggerFactory.getLogger(WorkerTask.class);

    private Worker worker;
    private TCPServerConfig tcpServerConfig;

    public WorkerTask(Worker worker, TCPServerConfig tcpServerConfig) {
        this.worker = worker;
        this.tcpServerConfig = tcpServerConfig;
    }

    /**
     * 轮询执行队列里的任务
     */
    @Override
    public void run() {
        try{
            /* 获取协议解析器 */
            ProtocolCodec protocolCodec = tcpServerConfig.getProtocolCodec();
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
            logger.error("WorkerThread出现异常", e);
            if(worker != null){
                worker.destroy();
            }
        } finally {
            /* 设置成WAIT状态，允许下次执行 */
            if(worker != null){
                worker.setStatusEnums(StatusEnums.WAIT);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        WorkerTask workerTask = (WorkerTask) obj;
        if(workerTask.worker == this.worker){
            return true;
        }
        return false;
    }
}
