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

    public static MagicianConfig create(){
        return new MagicianConfig();
    }

    public int getBossThreads() {
        return bossThreads;
    }

    public MagicianConfig setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
        return this;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public MagicianConfig setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
        return this;
    }

    public int getNumberOfPorts() {
        return numberOfPorts;
    }

    public MagicianConfig setNumberOfPorts(int numberOfPorts) {
        this.numberOfPorts = numberOfPorts;
        return this;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public MagicianConfig setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public MagicianConfig setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public MagicianConfig setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public LogLevel getNettyLogLevel() {
        return nettyLogLevel;
    }

    public MagicianConfig setNettyLogLevel(LogLevel nettyLogLevel) {
        this.nettyLogLevel = nettyLogLevel;
        return this;
    }

    public int getMaxInitialLineLength() {
        return maxInitialLineLength;
    }

    public MagicianConfig setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
        return this;
    }

    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    public MagicianConfig setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
        return this;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public MagicianConfig setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
        return this;
    }
}
