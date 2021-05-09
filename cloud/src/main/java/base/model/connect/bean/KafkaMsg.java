package base.model.connect.bean;

import lombok.Data;
import base.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:54
 * @description：kafka 接收发送消息格式
 */
@Data
public class KafkaMsg implements BasicCommon {
    private String topic;
    private String key;
    private String value;

    @Override
    public String toString() {
        return "KafkaMsg{" +
                "topic='" + topic + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
