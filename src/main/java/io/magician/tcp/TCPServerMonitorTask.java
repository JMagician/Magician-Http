package io.magician.tcp;

import io.magician.common.util.ChannelUtil;
import io.magician.common.util.ReadUtil;
import io.magician.tcp.attach.AttachUtil;
import io.magician.tcp.attach.AttachmentModel;
import io.magician.common.event.EventGroup;
import io.magician.common.event.EventTask;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.workers.task.WorkerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 监听Selector的任务
 */
public class TCPServerMonitorTask implements EventTask {

    private static Logger logger = LoggerFactory.getLogger(TCPServerMonitorTask.class);

    /**
     * 通道，每个连接对应一个
     */
    private ServerSocketChannel serverSocketChannel;
    /**
     * 配置，创建服务时传入
     */
    private TCPServerConfig tcpServerConfig;
    /**
     * IO事件执行器组合
     * 一个端口 对应一个事件执行器
     */
    private EventGroup ioEventGroup;
    /**
     * 业务事件执行器组合
     * 一个连接对应一个事件执行器 + N个事件
     */
    private EventGroup workerEventGroup;

    /**
     * 创建一个端口监听任务
     * @param serverSocketChannel
     * @param tcpServerConfig
     * @param ioEventGroup
     * @param workerEventGroup
     */
    public TCPServerMonitorTask(ServerSocketChannel serverSocketChannel, TCPServerConfig tcpServerConfig, EventGroup ioEventGroup, EventGroup workerEventGroup){
        this.serverSocketChannel = serverSocketChannel;
        this.tcpServerConfig = tcpServerConfig;
        this.ioEventGroup = ioEventGroup;
        this.workerEventGroup = workerEventGroup;
    }

    @Override
    public void run() throws Exception {
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int num = selector.select(3000);
            if (num <= 0) {
                continue;
            }

            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            if (selectionKeySet == null || selectionKeySet.size() < 1) {
                continue;
            }

            Iterator<SelectionKey> it = selectionKeySet.iterator();
            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();
                SocketChannel channel = null;
                try {
                    if (!selectionKey.isValid()) {
                        continue;
                    }
                    /* 连接事件 */
                    if (selectionKey.isAcceptable()) {
                        channel = ((ServerSocketChannel) selectionKey.channel()).accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ, new AttachmentModel());
                    }

                    /* read事件 */
                    if (selectionKey.isReadable()) {
                        channel = read(selectionKey);
                    }
                } catch (Exception e){
                    ChannelUtil.cancel(selectionKey);
                    ChannelUtil.close(channel);
                }
            }
            if(ioEventGroup.getThreadPool().isShutdown()){
                logger.error("ioEventGroup里的线程池关闭了，所以Selector也停止了");
                selector.close();
                serverSocketChannel.close();
                return;
            }
            selector.wakeup();
        }
    }

    /**
     * 读取数据
     * @param selectionKey
     * @throws Exception
     */
    private SocketChannel read(SelectionKey selectionKey) throws Exception {
        /* 创建一个临时容器，将当前channel里的数据都暂存在里面，一起丢给worker */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        /* 缓冲区，每轮循环读取的最大长度 */
        ByteBuffer readBuffer = ByteBuffer.allocateDirect(tcpServerConfig.getReadSize());

        /* 将这当前这一管子数据全部读出来 */
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        while (true){
            int size = channel.read(readBuffer);

            /* 小于0 表示客户端已经断开了，直接释放channel和key */
            if(size < 0){
                ChannelUtil.cancel(selectionKey);
                ChannelUtil.close(channel);
                return null;
            }

            /* 等于0 表示当前这一管子数据已经读完，跳出循环即可 */
            if(size == 0){
                break;
            }

            /* 如果读到了数据就追加到outputStream */
            ReadUtil.byteBufferToOutputStream(readBuffer, outputStream);
        }

        /* 如果没读到数据 就跳出当前方法 */
        if(outputStream.size() < 1){
            return channel;
        }

        /* 获取附件 */
        AttachmentModel attachmentModel = AttachUtil.getAttachmentModel(selectionKey);

        /* 将读到的数据添加到worker的流水线，给协议层处理 */
        Worker worker = attachmentModel.getWorker();
        worker.addPipeLine(outputStream);
        worker.setSocketChannel(channel);
        worker.setSelectionKey(selectionKey);

        /* 往工作事件组 添加事件 */
        AttachUtil.getRunner(attachmentModel, workerEventGroup)
                .addEvent(new WorkerTask(worker, tcpServerConfig));

        return channel;
    }
}
