package io.magician.tcp.cache;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ProtocolDataModel {

    private ByteArrayOutputStream byteArrayOutputStream;

    private SocketChannel socketChannel;

    private SelectionKey selectionKey;

    private String protocolType;

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        if(byteArrayOutputStream == null || byteArrayOutputStream.size() < 1){
            return;
        }

        if(this.byteArrayOutputStream == null){
            this.byteArrayOutputStream = new ByteArrayOutputStream();
        }
        this.byteArrayOutputStream.write(byteArrayOutputStream.toByteArray());
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

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }
}
