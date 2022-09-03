package io.magician.common.config;

import io.netty.handler.logging.LogLevel;

/**
 * Startup parameter configuration
 * All configuration items only apply to the HttpServer instance where they are located.
 * If you use the same MagicianConfig object in multiple instances, these instances will use this configuration
 */
public class MagicianConfig {

    /**
     * netty - number of boss threads
     */
    private int bossThreads = 1;
    /**
     * netty - number of worker threads
     */
    private int workThreads = 3;
    /**
     * The maximum number of ports allowed to listen
     */
    private int numberOfPorts = 1;

    /**
     * Business Thread Pool - Number of Core Threads
     */
    private int corePoolSize = 3;
    /**
     * Business Thread Pool - Maximum Number of Threads
     */
    private int maximumPoolSize = 10;
    /**
     * Business Thread Pool - Thread keepAlive Time
     */
    private long keepAliveTime = 60000;
    /**
     * log level of netty
     */
    private LogLevel nettyLogLevel = LogLevel.DEBUG;

    /* ********* Three construction parameters of HttpServerCodec ********* */
    private int maxInitialLineLength = 4096;
    private int maxHeaderSize = 8192;
    private int maxChunkSize = 8192;

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public int getNumberOfPorts() {
        return numberOfPorts;
    }

    public void setNumberOfPorts(int numberOfPorts) {
        this.numberOfPorts = numberOfPorts;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public LogLevel getNettyLogLevel() {
        return nettyLogLevel;
    }

    public void setNettyLogLevel(LogLevel nettyLogLevel) {
        this.nettyLogLevel = nettyLogLevel;
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
