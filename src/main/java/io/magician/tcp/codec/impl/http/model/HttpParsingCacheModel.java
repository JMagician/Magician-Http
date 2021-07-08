package io.magician.tcp.codec.impl.http.model;

import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;

/**
 * http解析器附件实体类
 */
public class HttpParsingCacheModel {

    /**
     * head是否已经读完
     */
    private boolean headEnd;

    /**
     * head长度
     */
    private int headLength;

    /**
     * 请求数据中转器
     */
    private MagicianHttpExchange magicianHttpExchange;

    public HttpParsingCacheModel(){
        this.headEnd = false;
    }

    public boolean isHeadEnd() {
        return headEnd;
    }

    public void setHeadEnd(boolean headEnd) {
        this.headEnd = headEnd;
    }

    public int getHeadLength() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        this.headLength = headLength;
    }

    public MagicianHttpExchange getMagicianHttpExchange() {
        return magicianHttpExchange;
    }

    public void setMagicianHttpExchange(MagicianHttpExchange magicianHttpExchange) {
        this.magicianHttpExchange = magicianHttpExchange;
    }
}
