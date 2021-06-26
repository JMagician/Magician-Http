package io.magician.tcp.codec.impl.websocket.parsing;

import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.impl.websocket.constant.WebSocketEnum;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * websocket报文解析
 */
public class WebSocketMessageParsing {

    /**
     * 报文数据
     */
    private ByteArrayOutputStream outputStream;

    /**
     * webSocket数据中转器
     */
    private WebSocketExchange webSocketExchange;

    /**
     * 构造函数
     * @param outputStream
     * @param webSocketExchange
     */
    public WebSocketMessageParsing(ByteArrayOutputStream outputStream, WebSocketExchange webSocketExchange){
        this.outputStream = outputStream;
        this.webSocketExchange = webSocketExchange;
    }

    /**
     * 解析报文
     * @return
     * @throws Exception
     */
    public WebSocketExchange completed() throws Exception {

        byte[] bytesData = outputStream.toByteArray();
        if (bytesData.length < 1) {
            return null;
        }

        int opcode = bytesData[0] & 0x0f;
        if (opcode == 8) {
            webSocketExchange.setWebSocketEnum(WebSocketEnum.CLOSE);
            return webSocketExchange;
        }
        if (bytesData.length < 2) {
            return null;
        }

        int payloadLength = (bytesData[1] & 0x7f);
        if(payloadLength < 1){
            return null;
        }
        if(payloadLength == 126){
            // TODO
        } else if(payloadLength == 127){
            // TODO
        }

        int maskEndIndex = 6;
        if(bytesData.length < (payloadLength + maskEndIndex)){
            return null;
        }
        byte[] mask = Arrays.copyOfRange(bytesData, 4, maskEndIndex);
        byte[] payloadData = Arrays.copyOfRange(bytesData, maskEndIndex, payloadLength + maskEndIndex);

        if(payloadData.length < payloadLength){
            return null;
        }

        for (int i = 0; i < payloadData.length; i++) {
            payloadData[i] = (byte) (payloadData[i] ^ mask[i % 4]);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(payloadData);

        webSocketExchange.setLength(maskEndIndex + outputStream.size());
        webSocketExchange.setOutputStream(outputStream);
        webSocketExchange.setWebSocketEnum(WebSocketEnum.MESSAGE);

        return webSocketExchange;
    }

    public static int bytesToInt16(byte[] src) {


        int value = (int) ((src[0]&0xFF)
                | ((src[1]<<8) & 0xFF00));
//
//        int value = ((src[0] & 0xFF)
//                | ((src[1] & 0xFF)<<8));
//                | ((src[2] & 0xFF)<<16));
        return value;
    }

    public static int bytesToInt64(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF)<<8)
                | ((src[2] & 0xFF)<<16));
        return value;
    }
}
