package example.cc3200.bean;

import example.cc3200.actor.CC3200ServerActor;
import lombok.Data;

@Data
public class KafkaData implements CC3200ServerActor.Command {
    private String topic;
    private String key;
    private String value;
}
