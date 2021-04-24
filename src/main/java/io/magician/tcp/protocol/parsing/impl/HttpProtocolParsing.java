package io.magician.tcp.protocol.parsing.impl;

import io.magician.common.constant.CommonConstant;
import io.magician.common.util.ByteUtil;
import io.magician.tcp.http.constant.HttpConstant;
import io.magician.tcp.http.parsing.ReadCompletionHandler;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.common.util.ChannelUtil;
import io.magician.tcp.protocol.model.ProtocolDataModel;
import io.magician.tcp.protocol.parsing.ProtocolParsing;
import io.magician.tcp.protocol.routing.RoutingParsing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP协议解析器
 */
public class HttpProtocolParsing implements ProtocolParsing<MagicianHttpExchange> {

    private Logger logger = LoggerFactory.getLogger(ReadCompletionHandler.class);

    private static Map<String, Boolean> methods = new HashMap<>();

    static {
        methods.put("GET", true);
        methods.put("POST", true);
        methods.put("DELETE", true);
        methods.put("PUT", true);
        methods.put("PATCH", true);
        methods.put("HEAD", true);
        methods.put("CONNECT", true);
        methods.put("OPTIONS", true);
        methods.put("TRACE", true);
        methods.put("MOVE", true);
        methods.put("COPY", true);
        methods.put("LINK", true);
        methods.put("UNLINK", true);
        methods.put("WRAPPED", true);
    }

    /**
     * 解析第一行，判断是否是http协议
     * @param protocolDataModel
     * @return
     */
    public boolean isThis(ProtocolDataModel protocolDataModel) throws Exception {
        if(protocolDataModel.getByteArrayOutputStream() == null || protocolDataModel.getByteArrayOutputStream().size() < 1){
            return false;
        }

        /* 如果是同一个连接的两次请求，就直接根据附件判断协议类型 */
        Object obj = protocolDataModel.getSelectionKey().attachment();
        if(obj != null && HttpConstant.TARGET.equals(obj.toString())){
            return true;
        }

        byte[] dataByte = protocolDataModel.getByteArrayOutputStream().toByteArray();
        /* 获取第一行换行符的位置 */
        int index = ByteUtil.byteIndexOf(dataByte,
                HttpConstant.CARRIAGE_RETURN.getBytes(CommonConstant.ENCODING));
        /* 如果没有换行符 那肯定不是http协议 */
        if(index < 0){
            return false;
        }

        /* 从报文中截取第一行 */
        byte[] firstLine = ByteUtil.subByte(dataByte, 0, index);
        String firstLineStr = new String(firstLine, CommonConstant.ENCODING);

        /* 如果第一行为空，那肯定不是http协议 */
        if(firstLineStr == null || firstLineStr.equals("")){
            return false;
        }

        /* 第一行不是被空格分成了三分，那肯定不是http协议 */
        String[] splitStr = firstLineStr.split("\\s+");
        if(splitStr.length < 3){
            return false;
        }

        /* 如果第一行第一个元素 不是http的请求方式，那肯定不是http协议 */
        if(methods.get(splitStr[0].toUpperCase()) == null){
            return false;
        }

        /* 如果第一行的最后一截是http开头的，那肯定是http协议 */
        if(splitStr[2].toUpperCase().startsWith(HttpConstant.TARGET)){
            protocolDataModel.getSelectionKey().attach(HttpConstant.TARGET);
            return true;
        }
        return false;
    }

    /**
     * 彻底解析报文
     * @param protocolDataModel
     * @return
     */
    public MagicianHttpExchange parsingData(ProtocolDataModel protocolDataModel) throws Exception {
        try {
            /* 解析报文 */
            MagicianHttpExchange magicianHttpExchange = new MagicianHttpExchange();
            magicianHttpExchange.setSocketChannel(protocolDataModel.getSocketChannel());
            magicianHttpExchange.setSelectionKey(protocolDataModel.getSelectionKey());

            return new ReadCompletionHandler(
                    magicianHttpExchange,
                    protocolDataModel.getByteArrayOutputStream()
            ).completed();
        } catch (Exception e){
            logger.error("读取数据异常", e);
            ChannelUtil.close(protocolDataModel.getSocketChannel());
            ChannelUtil.cancel(protocolDataModel.getSelectionKey());
            throw e;
        }
    }

    /**
     * 执行handler
     * @param magicianHttpExchange
     */
    public void execute(MagicianHttpExchange magicianHttpExchange) throws Exception {
        RoutingParsing.parsing(magicianHttpExchange);
    }
}
