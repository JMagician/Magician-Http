package io.magician.udp;

import io.magician.udp.handler.MagicianUDPHandler;
import io.magician.udp.parsing.ReceiveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class UDPServerCreate {

    private Logger log = LoggerFactory.getLogger(UDPServerCreate.class);

    /**
     * 绑定端口
     *
     * @param port
     * @return
     */
    public UDPServerCreate bind(int port) {
        UDPServerConfig.setPort(port);
        return this;
    }

    /**
     * 设置允许几个线程同时处理任务
     *
     * @param threadSize
     * @return
     */
    public UDPServerCreate threadSize(int threadSize) {
        UDPServerConfig.setThreadSize(threadSize);
        return this;
    }

    /**
     * 读取数据的缓冲区大小
     * @param readSize
     * @return
     */
    public UDPServerCreate readSize(int readSize){
        UDPServerConfig.setReadSize(readSize);
        return this;
    }

    /**
     * 读完数据后的联络器
     * @param magicianUDPHandler
     * @return
     */
    public UDPServerCreate handler(MagicianUDPHandler magicianUDPHandler){
        UDPServerConfig.setMagicianUDPHandler(magicianUDPHandler);
        return this;
    }

    /**
     * 启动UDP服务
     */
    public void start() {
        try {
            DatagramChannel dc = DatagramChannel.open();
            dc.configureBlocking(false);
            dc.bind(new InetSocketAddress(UDPServerConfig.getPort()));

            Selector select = Selector.open() ;
            dc.register(select, SelectionKey.OP_READ) ;

            log.info("启动UDP服务成功");

            while(true) {
                int num = select.select();
                if (num == 0) {
                    continue;
                }

                Set Keys = select.selectedKeys();
                Iterator it = Keys.iterator();
                while(it.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey)it.next();
                    it.remove();

                    DatagramChannel datagramChannel = (DatagramChannel)selectionKey.channel() ;
                    datagramChannel.configureBlocking(false);

                    /* 接收数据 */
                    ReceiveHandler.receive(datagramChannel);
                }

                select.wakeup();
            }
        } catch (Exception e) {
            log.error("启动UDP服务异常", e);
        }
    }
}
