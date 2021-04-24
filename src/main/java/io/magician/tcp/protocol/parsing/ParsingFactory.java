package io.magician.tcp.protocol.parsing;

import io.magician.tcp.HttpServerConfig;
import io.magician.tcp.protocol.model.ProtocolDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 协议解析器工厂
 */
public class ParsingFactory {

    private static Logger logger = LoggerFactory.getLogger(ParsingFactory.class);

    /**
     * 根据报文和其他信息判断是什么协议，然后返回对应的协议解析器
     * @param protocolDataModel
     * @return
     */
    public static ProtocolParsing getProtocolParsing(ProtocolDataModel protocolDataModel){
        for(ProtocolParsing protocolParsing : HttpServerConfig.getProtocolParsingList()){
            try {
                if(protocolParsing.isThis(protocolDataModel)){
                    return protocolParsing;
                }
            } catch (Exception e){
                logger.warn("解析数据包判断是什么协议的时候 出现异常", e);
            }
        }
        return null;
    }
}
