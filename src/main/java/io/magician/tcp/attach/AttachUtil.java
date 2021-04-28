package io.magician.tcp.attach;

import java.nio.channels.SelectionKey;

/**
 * 附件工具类
 */
public class AttachUtil {

    /**
     * 获取附件
     * @param selectionKey
     * @return
     */
    public static AttachmentModel getAttachmentModel(SelectionKey selectionKey){
        AttachmentModel attachmentModel = null;

        Object obj = selectionKey.attachment();
        if(obj == null){
            attachmentModel = new AttachmentModel();
            selectionKey.attach(attachmentModel);
        } else {
            attachmentModel = (AttachmentModel) obj;
        }
        return attachmentModel;
    }
}
