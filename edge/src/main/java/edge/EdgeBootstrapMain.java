package edge;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import edge.actor.PodActor;
import base.model.connect.bean.KafkaConfig;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 10:51
 * @description：边缘端启动入口
 */
public class EdgeBootstrapMain {
    private static Logger logger = Logger.getLogger(EdgeBootstrapMain.class.getName());

    public static void main(String[] args) {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "edge-bootstrap");
        init(system);
    }

    private static void init(ActorSystem<Void> system) {

        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServer("192.168.123.131:9092");
        kafkaConfig.setGroupId("1");
//        List<String> topics = new ArrayList<>();
//        topics.add("edge-pod-1");

//        List<String> lists = new ArrayList<>();
        kafkaConfig.setTopic("edge-pod-1");
//        kafkaConfig.setTopic(list);

        logger.log(Level.INFO, "edge-bootstrap init...");
        system.systemActorOf(PodActor.create(kafkaConfig), "edge-pod-1", Props.empty());
    }

}
