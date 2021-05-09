package cloud.bean;

import base.model.bean.BasicCommon;
import base.model.connect.bean.KafkaMsg;
import lombok.Data;

import java.util.List;

@Data
public class KafkaMsgList implements BasicCommon {
    private List<KafkaMsg> kafkaMsgs;
}
