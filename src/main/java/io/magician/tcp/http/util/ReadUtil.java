package io.magician.tcp.http.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * 读取工具类
 */
public class ReadUtil {

    /**
     * byteBuffer转outputStream
     * @param byteBuffer
     * @param outputStream
     * @throws Exception
     */
    public static void byteBufferToOutputStream(ByteBuffer byteBuffer, ByteArrayOutputStream outputStream) throws Exception {
        byteBuffer.flip();
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        writableByteChannel.write(byteBuffer);
        byteBuffer.clear();
    }
}
