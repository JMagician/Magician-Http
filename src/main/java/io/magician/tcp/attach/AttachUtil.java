package io.magician.tcp.attach;

import io.magician.common.event.EventGroup;
import io.magician.common.event.EventRunner;

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

    /**
     * 从附件中获取EventRunner
     * @param attachmentModel
     * @param eventGroup
     * @return
     */
    public static EventRunner getRunner(AttachmentModel attachmentModel, EventGroup eventGroup){
        EventRunner eventRunner = attachmentModel.getEventRunner();
        if(eventRunner == null){
            eventRunner = eventGroup.getEventRunner();
            attachmentModel.setEventRunner(eventRunner);
        }
        return eventRunner;
    }
}
