package io.magician.tcp.codec.impl.http.cache;

import io.magician.tcp.codec.impl.http.model.HttpParsingCacheModel;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.workers.Worker;

/**
 * http解析器附件管理
 */
public class HttpParsingCacheManager {

    /**
     * 获取附件
     * @param worker
     * @return
     */
    public static HttpParsingCacheModel getHttpParsingCacheModel(Worker worker){
        HttpParsingCacheModel parsingCacheModel = worker.getAttach(HttpParsingCacheModel.class);
        if(parsingCacheModel == null){
            parsingCacheModel = new HttpParsingCacheModel();

            MagicianHttpExchange magicianHttpExchange = new MagicianHttpExchange();
            magicianHttpExchange.setSocketChannel(worker.getSocketChannel());
            magicianHttpExchange.setSelectionKey(worker.getSelectionKey());

            parsingCacheModel.setMagicianHttpExchange(magicianHttpExchange);
            worker.setAttach(parsingCacheModel);
        }

        return parsingCacheModel;
    }

    /**
     * 清理附件
     * @param worker
     */
    public static void clear(Worker worker){
        worker.setAttach(null);
    }
}
