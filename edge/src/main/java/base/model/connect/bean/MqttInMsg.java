package base.model.connect.bean;

import base.model.bean.BasicCommon;
import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：从设备端接收到的MQTT消息
 */
@Data
public class MqttInMsg implements BasicCommon {
    private String topic;
    private String msg;
}
