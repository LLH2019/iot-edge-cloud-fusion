package things.model.connect.bean;

import lombok.Data;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：MQTT配置类
 */

@Data
public class MqttConfig {
    private List<String> topics;
    private String brokerUrl;
//    private String userName;
//    private String password;

//    private String clientId;

    @Override
    public String toString() {
        return "MqttConfig{" +
                "topic='" + topics + '\'' +
                ", brokerUrl='" + brokerUrl + '\'' +
//                ", userName='" + userName + '\'' +
//                ", password='" + password + '\'' +
//                ", clientId='" + clientId + '\'' +
                '}';
    }
}
