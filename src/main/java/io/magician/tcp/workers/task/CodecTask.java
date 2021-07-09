package io.magician.tcp.workers.task;

import io.magician.common.event.EventRunner;
import io.magician.tcp.TCPServerConfig;
import io.magician.common.event.EventTask;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.codec.ProtocolCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析报文的任务
 */
public class CodecTask implements EventTask {

    private Logger logger = LoggerFactory.getLogger(CodecTask.class);

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
     * 事件执行器
     */
    private EventRunner eventRunner;

    /**
     * 创建一个业务任务
     * @param worker
     * @param tcpServerConfig
     */
    public CodecTask(Worker worker, EventRunner eventRunner, TCPServerConfig tcpServerConfig) {
        this.worker = worker;
        this.eventRunner = eventRunner;
        this.tcpServerConfig = tcpServerConfig;
    }

    /**
     * 执行解析任务
     */
    @Override
    public void run() throws Exception {
        try{

            /* 获取协议解析器 */
            ProtocolCodec protocolCodec = tcpServerConfig.getProtocolCodec();
            if(protocolCodec == null){
                logger.error("请配置协议解码器");
                return;
            }
            Object resultObj = null;

            /*
             * 因为 EventRunner 如果空闲了，可以去其他EventRunner窃取任务
             * 而一旦窃取了任务，就意味着一个连接的多个事件 可能会出现并发执行，导致顺序被打乱
             * 执行顺序乱了就会导致 数据错乱，所以这里需要加个锁
             * 保证 每个连接的事件 按顺序执行（worker内部保证了取数据的顺序和存入的顺序一致）
             */
            synchronized (worker){
                /* 解析数据包 */
                if(worker.getSocketChannel().isOpen()){
                    resultObj = protocolCodec.codecData(worker, this.tcpServerConfig);
                }

                if(resultObj == null){
                    return;
                }
            }

            /* 往工作事件组添加任务，用来根据已经解析出来的数据 执行业务逻辑 */
            eventRunner.addEvent(new HandlerTask(resultObj, protocolCodec, worker, this.tcpServerConfig));

        } catch (Exception e){
            logger.error("解析报文出现异常", e);
            if(worker != null){
                worker.destroy();
            }
        }
    }
}
