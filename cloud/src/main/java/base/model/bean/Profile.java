package base.model.bean;

import lombok.Data;
import base.model.connect.bean.MqttConfig;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：静态属性
 */
@Data
public class Profile {
    private String productKey;
    private String productName;
    private MqttConfig mqttConfig;

}
