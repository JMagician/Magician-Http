package io.magician.tcp.codec.impl.websocket.parsing;

import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.impl.websocket.constant.WebSocketEnum;
import io.magician.tcp.cache.ProtocolDataModel;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * websocket报文解析
 */
public class WebSocketMessageParsing {

    /**
     * 报文数据
     */
    private ProtocolDataModel protocolDataModel;

    /**
     * webSocket数据中转器
     */
    private WebSocketExchange webSocketExchange;

    /**
     * 构造函数
     * @param protocolDataModel
     * @param webSocketExchange
     */
    public WebSocketMessageParsing(ProtocolDataModel protocolDataModel, WebSocketExchange webSocketExchange){
        this.protocolDataModel = protocolDataModel;
        this.webSocketExchange = webSocketExchange;
    }

    /**
     * 解析报文
     * @return
     * @throws Exception
     */
    public WebSocketExchange completed() throws Exception {

        byte[] bytesData = protocolDataModel.getByteArrayOutputStream().toByteArray();
        if (bytesData.length < 1) {
            return null;
        }

        int opcode = bytesData[0] & 0x0f;
        if (opcode == 8) {
            webSocketExchange.setWebSocketEnum(WebSocketEnum.CLOSE);
            return webSocketExchange;
        }
        if (bytesData.length < 6) {
            return null;
        }

        byte payloadLength = (byte) (bytesData[1] & 0x7f);
        byte[] mask = Arrays.copyOfRange(bytesData, 2, 6);
        byte[] payloadData = Arrays.copyOfRange(bytesData, 6, bytesData.length);
        for (int i = 0; i < payloadData.length; i++) {
            payloadData[i] = (byte) (payloadData[i] ^ mask[i % 4]);
        }

        if(payloadData.length < payloadLength){
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(payloadData);

        webSocketExchange.setOutputStream(outputStream);
        webSocketExchange.setWebSocketEnum(WebSocketEnum.MESSAGE);

        return webSocketExchange;
    }
}
