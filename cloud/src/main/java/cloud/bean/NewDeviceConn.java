package cloud.bean;

import lombok.Data;
import base.model.bean.BasicCommon;
import base.model.connect.bean.KafkaConfig;

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
