package base.model.bean;

import lombok.Data;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.MqttConfig;

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
                "model=" + model +
                ", mqttConfig=" + mqttConfig +
                ", kafkaConfig=" + kafkaConfig +
                '}';
    }
}
