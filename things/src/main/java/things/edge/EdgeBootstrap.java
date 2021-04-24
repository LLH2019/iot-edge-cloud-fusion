package things.edge;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import things.edge.actor.PodActor;
import things.model.actor.CC3200Actor;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.MqttConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 10:51
 * @description：边缘端启动入口
 */
public class EdgeBootstrap {

    public static void main(String[] args) {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "edge-bootstrap");
        init(system);
    }

    private static void init(ActorSystem<Void> system) {

        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServer("192.168.123.131:9092");
        kafkaConfig.setGroupId("1");
        List<String> topics = new ArrayList<>();
        topics.add("edge-pod-1");
        kafkaConfig.setTopics(topics);


        system.systemActorOf(PodActor.create(kafkaConfig, topics), "edge-pod-1", Props.empty());
    }

}
