package base.msg.handler;

import base.msg.bean.Message;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 17:18
 * @description：对消息进行处理的类
 */
public class MessageHandler {

    public Message handleMqttUpMsg(String msg) {
        Message message = new Message();
        message.setType(Message.MessageType.DEVICE_UP);
        message.setContent(msg);
        return message;
    }

}
