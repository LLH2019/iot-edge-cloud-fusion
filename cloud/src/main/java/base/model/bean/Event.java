package base.model.bean;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 21:36
 * @description：终端设备上报事件
 */
@Data
public class Event {
    private String name;
//    private String value;
    private String mqttTopic;

}
