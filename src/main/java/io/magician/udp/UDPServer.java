package io.magician.udp;

import io.magician.udp.load.LoadUDPResource;
import io.magician.udp.workers.ReceiveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

public class UDPServer {

    private Logger log = LoggerFactory.getLogger(UDPServer.class);

    /**
     * 设置线程池
     *
     * @param executor
     * @return
     */
    public UDPServer threadPool(Executor executor) {
        UDPServerConfig.setThreadPool(executor);
        return this;
    }

    /**
     * 读取数据的缓冲区大小
     *
     * @param readSize
     * @return
     */
    public UDPServer readSize(int readSize) {
        UDPServerConfig.setReadSize(readSize);
        return this;
    }

    /**
     * 扫描需要的资源
     *
     * @param packageName
     * @return
     */
    public UDPServer scan(String packageName) throws Exception {
        LoadUDPResource.loadHandler(packageName);
        return this;
    }

    /**
     * 启动UDP服务
     */
    public void bind(int port) throws Exception {

        DatagramChannel dc = DatagramChannel.open();
        dc.configureBlocking(false);
        dc.bind(new InetSocketAddress(port));

        Selector select = Selector.open();
        dc.register(select, SelectionKey.OP_READ);

        log.info("启动UDP服务成功");

        while (true) {
            int num = select.select();
            if (num == 0) {
                continue;
            }

            Set<SelectionKey> keys = select.selectedKeys();
            if(keys == null || keys.size() < 1){
                continue;
            }
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                try {
                    SelectionKey selectionKey = it.next();
                    it.remove();

                    DatagramChannel datagramChannel = (DatagramChannel) selectionKey.channel();
                    datagramChannel.configureBlocking(false);

                    /* 接收数据 */
                    ReceiveHandler.receive(datagramChannel);
                } catch (Exception e) {
                    log.error("UDP服务异常", e);
                }
            }
            select.wakeup();
        }
    }
}
