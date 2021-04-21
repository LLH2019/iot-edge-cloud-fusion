package things.model.bean;

import lombok.Data;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.MqttConfig;

import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：物模型抽象类
 */
@Data
public class AbstractModel implements BasicCommon {
    private Status status;

    private String name;

    private Profile profile;

    private List<Property> properties;

    private List<Event> events;

    private MqttConfig mqttConfig;

    private KafkaConfig kafkaConfig;

    public void setStatus(Status.ThingStatus offline) {
    }
}
