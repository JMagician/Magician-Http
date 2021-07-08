package io.magician.tcp.codec.impl.websocket.parsing;

import io.magician.common.util.ByteUtil;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.impl.websocket.constant.WebSocketEnum;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * websocket报文解析
 */
public class WebSocketMessageParsing {

    /**
     * 解析报文
     *
     * @return
     * @throws Exception
     */
    public static WebSocketExchange completed(ByteArrayOutputStream outputStream, WebSocketExchange webSocketExchange) throws Exception {

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
        if (payloadLength < 1) {
            return null;
        }

        int maskStartIndex = 2;

        if (payloadLength == 126) {
            byte[] len = getLength(bytesData, 2, 2);
            payloadLength = ByteUtil.bytesToInt(len, 0, len.length);
            maskStartIndex = 4;
        } else if (payloadLength == 127) {
            byte[] len = getLength(bytesData, 2, 8);
            payloadLength = ByteUtil.bytesToInt(len, 0, len.length);
            maskStartIndex = 10;
        }

        int maskEndIndex = maskStartIndex + 4;
        if (bytesData.length < (payloadLength + maskEndIndex)) {
            return null;
        }
        byte[] mask = Arrays.copyOfRange(bytesData, maskStartIndex, maskEndIndex);
        byte[] payloadData = Arrays.copyOfRange(bytesData, maskEndIndex, payloadLength + maskEndIndex);

        if (payloadData.length < payloadLength) {
            return null;
        }

        for (int i = 0; i < payloadData.length; i++) {
            payloadData[i] = (byte) (payloadData[i] ^ mask[i % 4]);
        }

        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(payloadData);

        webSocketExchange.setLength(maskEndIndex + message.size());
        webSocketExchange.setOutputStream(message);
        webSocketExchange.setWebSocketEnum(WebSocketEnum.MESSAGE);

        return webSocketExchange;
    }

    /**
     * 获取websocket数据长度
     *
     * @param bytesData
     * @param start
     * @param size
     * @return
     */
    private static byte[] getLength(byte[] bytesData, int start, int size) {
        int index = 0;
        byte[] len = new byte[size];
        for (int i = start; i < (start + size); i++) {
            len[index] = bytesData[i];
            index++;
        }
        return len;
    }
}
