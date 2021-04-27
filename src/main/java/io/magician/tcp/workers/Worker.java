package io.magician.tcp.workers;

import io.magician.common.constant.StatusEnums;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 工作者，每个连接对应一个对象
 */
public class Worker {

    /**
     * 流水线，selector从channel读到了数据 都会追加进来
     */
    private volatile List<ByteArrayOutputStream> pipeLine = new LinkedList<>();

    /**
     * 数据缓存，解码器解码的时候会将流水线上的数据都合并到这里
     * 如果里面已经包含了一个完整的报文，那么就将这个完整报文拿走去执行业务逻辑，留下剩余数据，实现拆包
     * 由于worker是单线程的，所以这个缓存是线程安全的
     */
    private volatile ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    /**
     * NIO原生管道，留给后面的业务逻辑用的
     * 比如: 响应数据
     */
    private volatile SocketChannel socketChannel;
    /**
     * NIO原生key，留给后面的业务逻辑用的
     * 比如: 添加附件
     */
    private volatile SelectionKey selectionKey;

    /**
     * worker状态，默认是等待工作
     * 如果RUNNING 状态，那么NIOSelector只会将数据追加到pipeLine，而WorkerSelector不会将这个worker放入线程池
     * 如果是WAIT 状态，那么selector将数据追加到pipeLine后，WorkerSelector还会将它放入线程池
     * 等执行这个worker的线程完成后，会将这个字段重新改为WAIT状态
     */
    private volatile StatusEnums statusEnums = StatusEnums.WAIT;

    /**
     * 是否可读状态
     * NIOSelector每次读到了数据都会把这个worker改为OP_READ状态，这样WorkerSelector将会发现这个worker从而给它指派工作
     * 解码器每次读完数据后都会把这个worker改为OP_ACCEPT状态，从而阻止WorkerSelector重复消费
     */
    private volatile int opStatus = SelectionKey.OP_WRITE;

    /**
     * 添加数据到流水线
     * @param byteArrayOutputStream
     * @throws Exception
     */
    public void addPipeLine(ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream == null || byteArrayOutputStream.size() < 1) {
            return;
        }

        synchronized (this.pipeLine) {
            if (this.pipeLine == null) {
                this.pipeLine = new LinkedList<>();
            }

            this.pipeLine.add(byteArrayOutputStream);
            opStatus = SelectionKey.OP_READ;
        }
    }

    /**
     * 将流水线数据合并到缓存
     * @return
     * @throws Exception
     */
    public ByteArrayOutputStream getOutputStream() throws Exception {
        synchronized (this.pipeLine) {
            Iterator<ByteArrayOutputStream> it = this.pipeLine.iterator();
            while (it.hasNext()) {
                ByteArrayOutputStream item = it.next();
                if(item != null && item.size() > 0){
                    outputStream.write(item.toByteArray());
                }
                it.remove();
            }
            opStatus = SelectionKey.OP_ACCEPT;
            return outputStream;
        }
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public StatusEnums getStatusEnums() {
        return statusEnums;
    }

    public void setStatusEnums(StatusEnums statusEnums) {
        this.statusEnums = statusEnums;
    }

    public int getOpStatus() {
        return opStatus;
    }

    /**
     * 从缓存中去除已经被业务使用的那部分数据
     * @param skip
     * @throws Exception
     */
    public void skipOutputStream(int skip) throws Exception {
        byte[] dataBytes = outputStream.toByteArray();
        byte[] newBytes = new byte[dataBytes.length - skip];

        System.arraycopy(dataBytes, skip, newBytes, 0, newBytes.length);

        outputStream.reset();
        outputStream.write(newBytes);
    }
}
