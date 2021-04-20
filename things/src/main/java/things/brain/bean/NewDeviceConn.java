package things.brain.bean;

import lombok.Data;
import things.model.bean.BasicCommon;
import things.model.connect.bean.KafkaConfig;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 22:01
 * @description：
 */
@Data
public class NewDeviceConn implements BasicCommon {
    private KafkaConfig kafkaConfig;
    private String name;

}
