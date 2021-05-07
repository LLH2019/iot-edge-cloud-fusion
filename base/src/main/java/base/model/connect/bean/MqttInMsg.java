package base.model.connect.bean;

import lombok.Data;
import base.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：从设备端接收到的MQTT消息
 */
@Data
public class MqttInMsg implements BasicCommon {
    private final String msg;
}
