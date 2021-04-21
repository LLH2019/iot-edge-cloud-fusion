package things.model.connect.bean;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：MQTT配置类
 */

@Data
public class MqttConfig {
    private String topic;
    private String brokerUrl;
    private String userName;
    private String password;

    private String clientId;

    public MqttConfig() {

    }

    public MqttConfig(String topic, String brokerUrl, String userName, String password, String clientId) {
        this.topic = topic;
        this.brokerUrl = brokerUrl;
        this.userName = userName;
        this.password = password;
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "MqttConfig{" +
                "topic='" + topic + '\'' +
                ", brokerUrl='" + brokerUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
