package io.magician.common.config;

import io.netty.handler.logging.LogLevel;

/**
 * 启动参数配置
 * 所有配置项只作用于所在的HttpServer实例，如果你在多个实例中都用了同一个MagicianConfig对象，那么这些实例都会采用这套配置
 */
public class MagicianConfig {

    /**
     * netty服务boss线程数
     */
    private int bossThreads = 1;
    /**
     * netty服务work线程数
     */
    private int workThreads = 3;
    /**
     * 最多允许监听几个端口
     */
    private int numberOfPorts = 1;
    /**
     * netty的日志级别
     */
    private LogLevel nettyLogLevel = LogLevel.DEBUG;

    /* ********* HttpServerCodec 的三个构造参数 ********* */
    private int maxInitialLineLength = 4096;
    private int maxHeaderSize = 8192;
    private int maxChunkSize = 8192;

    public Integer getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(Integer bossThreads) {
        this.bossThreads = bossThreads;
    }

    public Integer getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(Integer workThreads) {
        this.workThreads = workThreads;
    }

    public LogLevel getNettyLogLevel() {
        return nettyLogLevel;
    }

    public void setNettyLogLevel(LogLevel nettyLogLevel) {
        this.nettyLogLevel = nettyLogLevel;
    }

    public Integer getNumberOfPorts() {
        return numberOfPorts;
    }

    public void setNumberOfPorts(Integer numberOfPorts) {
        this.numberOfPorts = numberOfPorts;
    }

    public int getMaxInitialLineLength() {
        return maxInitialLineLength;
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
    }

    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }
}
