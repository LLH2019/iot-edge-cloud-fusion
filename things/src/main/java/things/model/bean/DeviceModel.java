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
public class DeviceModel extends AbstractModel implements BasicCommon {
    private Status status;

    private MqttConfig mqttConfig;

    private KafkaConfig kafkaConfig;

    public DeviceModel() {
    }

    public DeviceModel(Status status, String name, Profile profile, List<Property> properties, List<Event> events, MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        this.status = status;
        this.setName(name);
        this.setProfile(profile);
        this.setProperties(properties);
        this.setEvents(events);
        this.mqttConfig = mqttConfig;
        this.kafkaConfig = kafkaConfig;
    }

    public void setStatus(Status.ThingStatus offline) {
    }

    @Override
    public String toString() {
        return "AbstractModel{" +
                "status=" + status +
                ", name='" + this.getName() + '\'' +
                ", profile=" + this.getProfile() +
                ", properties=" + this.getProperties() +
                ", events=" + this.getEvents() +
                ", mqttConfig=" + mqttConfig +
                ", kafkaConfig=" + kafkaConfig +
                '}';
    }
}
