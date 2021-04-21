package things.model.connect.bean;

import lombok.Data;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：Kafka配置类
 */

@Data
public class KafkaConfig {
    private String server;
    private String groupId;
    private List<String> topics;
//    private

    public KafkaConfig() {

    }

    public KafkaConfig(String server, String groupId, List<String> topics) {
        this.server = server;
        this.groupId = groupId;
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "KafkaConfig{" +
                "server='" + server + '\'' +
                ", groupId='" + groupId + '\'' +
                ", topics=" + topics +
                '}';
    }
}
