package base.msg.bean;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 17:08
 * @description：基本消息体
 */
@Data
public class Message {

    // 初始连接消息
    // 终止连接消息
    // 设备上传消息
    // 控制设备消息
    private MessageType type;
    private Boolean isPersist;
    private String content;

    public enum MessageType {
        DEVICE_UP,
        CONTROL_DEVICE
    }

}
