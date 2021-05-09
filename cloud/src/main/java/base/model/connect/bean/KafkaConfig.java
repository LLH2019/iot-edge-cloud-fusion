package base.model.connect.bean;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：Kafka配置类
 */

@Data
public class KafkaConfig {
    private String server;
    private String groupId;
    private String topic;
//    private

    public KafkaConfig() {

    }

    public KafkaConfig(String server, String groupId, String topic) {
        this.server = server;
        this.groupId = groupId;
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "KafkaConfig{" +
                "server='" + server + '\'' +
                ", groupId='" + groupId + '\'' +
                ", topic=" + topic +
                '}';
    }
}
