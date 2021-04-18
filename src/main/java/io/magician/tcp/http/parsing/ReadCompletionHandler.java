package io.magician.tcp.http.parsing;

import io.magician.tcp.HttpServerConfig;
import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.http.util.ChannelUtil;
import io.magician.tcp.http.util.ReadUtil;
import io.magician.tcp.routing.RoutingParsing;
import io.magician.tcp.http.constant.ReqMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * 读取数据
 */
public class ReadCompletionHandler extends ReadFields {

    private Logger logger = LoggerFactory.getLogger(ReadCompletionHandler.class);

    /**
     * 构造函数
     * @param magicianHttpExchange
     */
    public ReadCompletionHandler(MagicianHttpExchange magicianHttpExchange){
        this.magicianHttpExchange = magicianHttpExchange;
        this.channel = magicianHttpExchange.getSocketChannel();
        startIndex = 0;
        endIndex = 0;
    }

    /**
     * 读取请求数据
     */
    public void completed() {
        try {
            /* 解析读到的数据 */
            ByteBuffer readBuffer = ByteBuffer.allocate(800);
            while (this.channel.read(readBuffer) > -1) {
                readBuffer = parsingByByteBuffer(readBuffer);
                if(readBuffer == null){
                    break;
                }
            }

            /* **********************如果读完了，就执行业务逻辑********************** */

            /* 过滤掉非法请求, 理论上这块代码已经不需要了, 但是保险起见 就没删除 */
            if(magicianHttpExchange.getRequestURI() == null
                    || magicianHttpExchange.getRequestMethod() == null
                    || magicianHttpExchange.getHttpVersion() == null){
                ChannelUtil.close(this.channel);
                return;
            }

            /* 获取body */
            getBody();

            /* 根据解析结果判断是不是websocket 然后进行分别处理 */
            RoutingParsing.parsing(magicianHttpExchange);
        } catch (Exception e){
            logger.error("读取数据异常", e);
            ChannelUtil.close(this.channel);
        }
    }

    /**
     * 从ByteBuffer解析数据
     * @param readBuffer
     * @return
     * @throws Exception
     */
    private ByteBuffer parsingByByteBuffer(ByteBuffer readBuffer) throws Exception {
        /* 将本次读取到的数据追加到输出流 */
        ReadUtil.byteBufferToOutputStream(readBuffer, outputStream);

        /* 判断是否已经把头读完了 */
        if (!readHead) {
            int length = headIndexOf();
            if (length < 0) {
                return readBuffer;
            }

            /* 解析头并获取头的长度 */
            headLength = parseHeader(length);
            readHead = true;
            /* 如果头读完了，并且此次请求是GET，则停止 */
            if (magicianHttpExchange.getRequestMethod().toUpperCase().equals(ReqMethod.GET.toString())) {
                return null;
            }

            /* 从head获取到Content-Length */
            contentLength = magicianHttpExchange.getRequestContentLength();
            if (contentLength < 0) {
                return null;
            }
        }
        /* 判断已经读取的body长度是否等于Content-Length，如果条件满足则说明读取完成 */
        int streamLength = outputStream.size();
        if ((streamLength - headLength) >= contentLength) {
            return null;
        }

        /* 当请求头读完了以后，就加大每次读取大小 加快速度 */
        readBuffer = ByteBuffer.allocate(HttpServerConfig.getReadSize());
        readBuffer.clear();

        return readBuffer;
    }

    /**
     * 截取请求头
     * @param length
     * @return
     */
    private byte[] subHead(int length){
        if(length <= 0){
            return new byte[1];
        }

        byte[] nowBytes = outputStream.toByteArray();
        byte[] bytes = new byte[length];
        for(int i=0;i<length;i++){
            bytes[i] = nowBytes[i];
        }
        return bytes;
    }

    /**
     * 获取请求头的结束坐标
     * @return -1 表示头还没读完，>-1 表示头的结束位置（不包含\r\n\r\n）
     * @throws Exception
     */
    private int headIndexOf() throws Exception {
        byte[] nowBytes = outputStream.toByteArray();
        if(headEndBytes == null){
            headEndBytes = MagicianConstant.HEAD_END.getBytes(MagicianConstant.ENCODING);
        }

        /* 这两个变量设置成全局，是为了避免每次都被初始化，从而实现从上次的坐标继续找，节约查找次数 */
        startIndex = 0;
        endIndex = headEndBytes.length;

        while (true){
            int index = 0;
            boolean exist = true;

            /* 如果剩余长度已经 小于 结束符的长度 就不用继续了 */
            if((nowBytes.length - startIndex) < headEndBytes.length){
                return -1;
            }
            /* 从startIndex开始比较，往后比较到endIndex的位置，如果全都相等就说明找到了了头的结束符 */
            for(int i=startIndex; i<endIndex; i++){
                if(index > headEndBytes.length - 1){
                    return -1;
                }
                /* 只要有一个不相同就说明不相同，重新设置坐标，再次比较 */
                if(nowBytes[i] != headEndBytes[index]){
                    startIndex++;
                    endIndex++;
                    if(startIndex > (nowBytes.length-1) || endIndex > nowBytes.length){
                        /* 如果坐标已经超出数据的范围了，说明没找到 */
                        return -1;
                    }
                    exist = false;
                    break;
                }
                index++;
            }
            if(exist){
                /* 如果exist等于true，说明上面的for循环里没有进入过if，也就是说已经找到结束符了 */
                return startIndex;
            }
        }
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
    private int parseHeader(int length) throws Exception {
        String headStr = new String(subHead(length));
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
