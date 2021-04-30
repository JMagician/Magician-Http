package io.magician.tcp.workers;

import io.magician.common.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 工作者，每个连接对应一个对象
 */
public class Worker {

    private Logger logger = LoggerFactory.getLogger(Worker.class);

    /**
     * 流水线，selector从channel读到了数据 都会追加进来
     */
    private volatile LinkedBlockingQueue<byte[]> pipeLine = new LinkedBlockingQueue();

    /**
     * 数据缓存，解码器解码的时候会将流水线上的数据都合并到这里
     * 如果里面已经包含了一个完整的报文，那么就将这个完整报文拿走去执行业务逻辑，留下剩余数据，实现拆包
     * 这个缓存只有解码器会用，同一个连接，同时只会有一个线程运行解码器，所以这个缓存是线程安全的
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
     * 添加数据到流水线
     * @param byteArrayOutputStream
     * @throws Exception
     */
    public void addPipeLine(ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream == null || byteArrayOutputStream.size() < 1) {
            return;
        }
        try {
            if (this.pipeLine == null) {
                this.pipeLine = new LinkedBlockingQueue();
            }

            this.pipeLine.add(byteArrayOutputStream.toByteArray());
        } catch (Exception e){
            logger.error("给Worker追加新数据异常", e);
        }
    }

    /**
     * 将流水线数据合并到缓存
     * @return
     * @throws Exception
     */
    public ByteArrayOutputStream getOutputStream() throws Exception {
        if(this.pipeLine == null || this.pipeLine.size() < 1){
            return outputStream;
        }

        byte[] first = this.pipeLine.poll();
        outputStream.write(first);

        return outputStream;
    }

    /**
     * 解码器每次读取了pipeLine 都会清空它
     * 所以只要pipeLine不为空，就说明有新数据可用
     * @return
     */
    public boolean isRead(){
        return this.pipeLine != null && this.pipeLine.size() > 0;
    }

    /**
     * 从缓存中去除已经被业务使用的那部分数据
     * @param skip
     * @return false 没有剩余数据了，
     * @throws Exception
     */
    public void skipOutputStream(int skip) throws Exception {
        if(skip >= outputStream.size()){
            outputStream.reset();
            return;
        }

        byte[] dataBytes = outputStream.toByteArray();
        byte[] newBytes = new byte[dataBytes.length - skip];

        System.arraycopy(dataBytes, skip, newBytes, 0, newBytes.length);

        outputStream.reset();
        outputStream.write(newBytes);
    }

    /**
     * 销毁worker
     */
    public void destroy(){
        ChannelUtil.close(socketChannel);
        ChannelUtil.cancel(selectionKey);
        pipeLine.clear();
        clear();
    }

    /**
     * 清理缓存
     * 如果协议类型的性质 可以决定当前获取的数据 全部都是只给当前解码器用的
     * 那么，在当前解码器获取完整数据后 可以直接清理缓存
     */
    public void clear(){
        outputStream.reset();
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
}
