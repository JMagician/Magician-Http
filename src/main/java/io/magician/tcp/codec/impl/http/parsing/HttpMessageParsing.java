package io.magician.tcp.codec.impl.http.parsing;

import io.magician.tcp.codec.impl.http.constant.HttpConstant;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.common.constant.CommonConstant;
import io.magician.common.util.ByteUtil;
import io.magician.tcp.codec.impl.http.constant.ReqMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * http报文解析
 */
public class HttpMessageParsing extends ReadFields {

    /**
     * head结束符
     */
    private static byte[] headEnd;

    /**
     * 构造函数
     * @param magicianHttpExchange
     */
    public HttpMessageParsing(MagicianHttpExchange magicianHttpExchange, ByteArrayOutputStream outputStream){
        this.magicianHttpExchange = magicianHttpExchange;
        this.outputStream = outputStream;
    }

    /**
     * 读取请求数据
     * @return
     */
    public MagicianHttpExchange completed() throws Exception {
        if(headEnd == null){
            headEnd = HttpConstant.HEAD_END.getBytes(CommonConstant.ENCODING);
        }

        /* 查找head结束符，如果没找到就返回-1，找到了就返回位置 */
        int length = ByteUtil.byteIndexOf(outputStream.toByteArray(), headEnd);
        if (length < 0) {
            return null;
        }

        /* 根据head的位置 将head读取出来，并返回head的长度 */
        headLength = parseHeader(length);

        /* 如果是get请求，那么头读完也就结束了 */
        if(ReqMethod.GET.toString().toUpperCase().equals(magicianHttpExchange.getRequestMethod().toUpperCase())){
            return magicianHttpExchange;
        }

        /* 如果不是get请求，就要获取content-length */
        long contentLength = magicianHttpExchange.getRequestContentLength();

        /*
         * 如果头读完了，但是没有content-length， 这是属于不正常的现象
         * 但是他毕竟真的读到了数据，所以就当他已经完了吧，返回数据让后面的逻辑尝试执行handler
         */
        if(contentLength < 0){
            getBody();
            return magicianHttpExchange;
        }

        /* 报文长度-head长度 如果 < 内容长度，就说明还没读完 */
        if ((outputStream.size() - headLength) < contentLength) {
            return null;
        }

        /* 获取body */
        getBody();
        return magicianHttpExchange;
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
     * 读取请求头
     *
     * @throws Exception
     */
    private int parseHeader(int length) throws Exception {
        String headStr = new String(subHead(length));
        String[] headers = headStr.split(HttpConstant.CARRIAGE_RETURN);
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
            String[] header = head.split(HttpConstant.SEPARATOR);
            if (header.length < 2) {
                continue;
            }
            magicianHttpExchange.setRequestHeader(header[0].trim(), header[1].trim());
        }

        return (headStr + HttpConstant.HEAD_END).getBytes(CommonConstant.ENCODING).length;
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
