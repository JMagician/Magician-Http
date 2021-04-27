package io.magician.tcp;

import io.magician.common.util.ChannelUtil;
import io.magician.common.util.ReadUtil;
import io.magician.tcp.workers.WorkersCacheManager;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.workers.selector.WorkerSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 处理请求
 */
public class TCPServerMonitor {

    private static Logger logger = LoggerFactory.getLogger(TCPServerMonitor.class);

    /**
     * 用Selector监控tcp连接
     * @param serverSocketChannel
     */
    public static void doMonitor(ServerSocketChannel serverSocketChannel) throws Exception {
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){
            try {
                int num = selector.select();
                if (num <= 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeySet.iterator();
                while (it.hasNext()) {
                    SelectionKey selectionKey = it.next();
                    it.remove();

                    if(!selectionKey.isValid()){
                        continue;
                    }
                    if(selectionKey.isAcceptable()){
                        SocketChannel channel = ((ServerSocketChannel) selectionKey.channel()).accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    } else if(selectionKey.isReadable()){
                        read(selectionKey);
                    }
                }
            } catch (Exception e){
                logger.error("Selector出现异常", e);
            }
        }
    }

    /**
     * 读取数据
     * @param selectionKey
     * @throws Exception
     */
    private static void read(SelectionKey selectionKey) throws Exception {
        SocketChannel channel = (SocketChannel) selectionKey.channel();

        /* 将这当前一个管子数据全部读出来 */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteBuffer readBuffer = ByteBuffer.allocate(TCPServerConfig.getReadSize());

        while (true){
            int size = channel.read(readBuffer);
            /* 小于0 表示客户端已经断开了 */
            if(size < 0){
                /* 如果客户端已经断开，并且之前的循环也没读到数据，那就直接释放channel和key */
                if(outputStream.size() < 1){
                    ChannelUtil.cancel(selectionKey);
                    ChannelUtil.close(channel);
                    return;
                }
                break;
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
            return;
        }

        /* 将读到的数据添加到worker的流水线，给协议层处理 */
        Worker worker = WorkersCacheManager.get(channel);
        worker.addPipeLine(outputStream);
        worker.setSocketChannel(channel);
        worker.setSelectionKey(selectionKey);

        WorkersCacheManager.put(channel, worker);

        /* 通知worker选择器，有可读状态的worker */
        WorkerSelector.notifySelector();
    }
}
