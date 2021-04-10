package io.magician.tcp.http.parsing;

import io.magician.MagicianConfig;
import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.http.util.ChannelUtil;
import io.magician.tcp.routing.RoutingParsing;
import io.magician.tcp.http.constant.ReqMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;

/**
 * 读取数据
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private Logger logger = LoggerFactory.getLogger(ReadCompletionHandler.class);

    /**
     * 通道
     */
    private AsynchronousSocketChannel channel;
    /**
     * 请求对象
     */
    private MagicianHttpExchange magicianHttpExchange;
    /**
     * 是否已经读完head了
     */
    private boolean readHead = false;
    /**
     * head的长度，用来计算body长度
     */
    private int headLength = 0;
    /**
     * 内容长度
     */
    private long contentLength = Long.MAX_VALUE;
    /**
     * 读完了没
     */
    private boolean readOver = false;
    /**
     * 读取到的数据缓存到这里
     */
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    /**
     * 构造函数
     * @param magicianHttpExchange
     */
    public ReadCompletionHandler(MagicianHttpExchange magicianHttpExchange){
        this.channel = magicianHttpExchange.getSocketChannel();
        this.magicianHttpExchange = magicianHttpExchange;
    }

    /**
     * 读取请求数据
     * @param result
     * @param attachment
     */
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        try {
            /* 解析读到的数据 */
            if(result > 0){
                ByteBuffer readBuffer = parsingByByteBuffer(attachment);
                if(readBuffer != null){
                    /* 如果数据没读完，就接着读 */
                    channel.read(readBuffer,
                            MagicianConfig.getReadTimeout(),
                            TimeUnit.MILLISECONDS,
                            readBuffer,
                            this);
                    return;
                }
            }

            /* 如果读到的数据小于0，说明此次请求是一个无效请求 */
            if(!readOver){
                ChannelUtil.close(channel);
                return;
            }

            /* 如果读完了，就执行业务逻辑 */
            if(readOver){
                /* 过滤掉非法请求 */
                if(magicianHttpExchange.getRequestURI() == null
                        || magicianHttpExchange.getRequestMethod() == null
                        || magicianHttpExchange.getHttpVersion() == null){
                    ChannelUtil.close(channel);
                    return;
                }
                /* 如果数据没读完，会在第一段的if里被return掉，所以执行到这肯定是已经读完了，所以接着执行业务逻辑，并关闭通道 */
                getBody();
                /* 解析结果，判断是不是websocket 然后进行分别处理 */
                RoutingParsing.parsing(magicianHttpExchange);
            }
        } catch (Exception e){
            logger.error("读取数据异常", e);
            ChannelUtil.close(channel);
        }
    }

    /**
     * 读取异常处理
     * @param exc
     * @param attachment
     */
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        logger.error("读取数据异常", exc);
        ChannelUtil.close(channel);
    }

    /**
     * 从ByteBuffer解析数据
     * @param readBuffer
     * @return
     * @throws Exception
     */
    private ByteBuffer parsingByByteBuffer(ByteBuffer readBuffer) throws Exception {
        /* 将本次读取到的数据追加到输出流 */
        readData(readBuffer);

        /* 判断是否已经把头读完了 */
        if (!readHead) {
            String headStr = outputStream.toString(MagicianConstant.ENCODING);
            /* 如果出现了连续的两个换行，则代表头已经读完了 */
            int headEndIndex = headStr.indexOf(MagicianConstant.HEAD_END);
            if (headEndIndex < 0) {
                readOver = false;
                return readBuffer;
            }

            /* 解析头并获取头的长度 */
            headLength = parseHeader(headStr, headEndIndex);
            readHead = true;
            /* 如果头读完了，并且此次请求是GET，则停止 */
            if (magicianHttpExchange.getRequestMethod().toUpperCase().equals(ReqMethod.GET.toString())) {
                readOver = true;
                return null;
            }

            /* 从head获取到Content-Length */
            contentLength = magicianHttpExchange.getRequestContentLength();
            if (contentLength < 0) {
                readOver = true;
                return null;
            }
        }

        /* 判断已经读取的body长度是否等于Content-Length，如果条件满足则说明读取完成 */
        int streamLength = outputStream.size();
        if ((streamLength - headLength) >= contentLength) {
            readOver = true;
            return null;
        }

        readOver = false;

        /* 当请求头读完了以后，就加大每次读取大小 加快速度 */
        readBuffer = ByteBuffer.allocate(MagicianConfig.getReadSize());
        readBuffer.clear();

        return readBuffer;
    }

    /**
     * 读取数据
     *
     * @param readBuffer
     * @return
     */
    private void readData(ByteBuffer readBuffer) throws Exception {
        readBuffer.flip();
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        writableByteChannel.write(readBuffer);
        readBuffer.clear();
    }

    /**
     * 读取请求头
     *
     * @throws Exception
     */
    private int parseHeader(String headStr, int headEndIndex) throws Exception {
        headStr = headStr.substring(0, headEndIndex);

        String[] headers = headStr.split(MagicianConstant.CARRIAGE_RETURN);
        for (int i = 0; i < headers.length; i++) {
            String head = headers[i];
            if (i == 0) {
                /* 读取第一行 */
                readFirstLine(head);
                continue;
            }

            if (head == null || "".equals(head)) {
                continue;
            }

            /* 读取头信息 */
            String[] header = head.split(MagicianConstant.SEPARATOR);
            if (header.length < 2) {
                continue;
            }
            magicianHttpExchange.setRequestHeader(header[0].trim(), header[1].trim());
        }

        return (headStr + MagicianConstant.HEAD_END).getBytes(MagicianConstant.ENCODING).length;
    }

    /**
     * 解析第一行
     *
     * @param firstLine
     */
    private void readFirstLine(String firstLine) {
        String[] parts = firstLine.split("\\s+");

        /*
         * 请求头的第一行必须由三部分构成，分别为 METHOD PATH VERSION
         * 比如：GET /index.html HTTP/1.1
         */
        if (parts.length < 3) {
            return;
        }
        /* 解析开头的三个信息(METHOD PATH VERSION) */
        magicianHttpExchange.setRequestMethod(parts[0]);
        magicianHttpExchange.setRequestURI(parts[1]);
        magicianHttpExchange.setHttpVersion(parts[2]);
    }

    /**
     * 从报文中获取body
     *
     * @throws Exception
     */
    private void getBody() {
        if (outputStream == null || outputStream.size() < 1) {
            return;
        }
        ByteArrayInputStream requestBody = new ByteArrayInputStream(outputStream.toByteArray());
        /* 跳过head，剩下的就是body */
        requestBody.skip(headLength);

        magicianHttpExchange.setRequestBody(requestBody);
    }
}
