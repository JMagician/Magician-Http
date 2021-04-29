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

    /**
     * 一个工作者
     * 每个连接对应一个，主要用于缓存报文，给解码器使用
     */
    private Worker worker;
    /**
     * 配置类
     */
    private TCPServerConfig tcpServerConfig;

    /**
     * 创建一个业务任务
     * @param worker
     * @param tcpServerConfig
     */
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

    /**
     * 因为一个连接可能会出现很多个read事件
     * 但是每个read事件的数据 都会被同步到Worker对象里面去
     * 所以，如果队列里已经有一个任务了，就不需要再往里面添加了
     * 这个方法 主要用来防止一个队列出现两个处理同一段数据的任务，对资源造成浪费的
     * @param obj
     * @return
     */
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
