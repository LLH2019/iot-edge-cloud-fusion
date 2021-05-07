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
public class DeviceModel implements BasicCommon {
//    private String realName;
    private AbstractModel model;
//    private Status status;
//    private List<String> topics;

    private MqttConfig mqttConfig;

    private KafkaConfig kafkaConfig;

    public DeviceModel() {
    }

//    public DeviceModel(Status status, MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
//        this.status = status;
//        this.mqttConfig = mqttConfig;
//        this.kafkaConfig = kafkaConfig;
//    }
//
//    public void setStatus(Status.ThingStatus offline) {
//    }

    @Override
    public String toString() {
        return "DeviceModel{" +
//                "realName='" + realName + '\'' +
//                ", model=" + model +
//                ", status=" + status +
                ", mqttConfig=" + mqttConfig +
                ", kafkaConfig=" + kafkaConfig +
                '}';
    }
}
