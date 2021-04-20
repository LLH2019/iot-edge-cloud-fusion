package things.controller.actor;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import java.util.List;
import things.model.bean.BasicCommon;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;

import java.util.ArrayList;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 16:36
 * @description：CC3200 控制actor
 */
public class CC3200ControlActor extends CloudControlActorKafkaInAndOut {
    public CC3200ControlActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context, kafkaConfig);
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
        System.out.println("create CC3200ControlActor...");
        return Behaviors.setup(context -> new CC3200ControlActor(context, kafkaConfig));
    }

    @Override
    public void handleKafkaMsg(KafkaMsg kafkaMsg) {
        System.out.println("kafkaMsg : " + kafkaMsg);
    }

    public static void main(String[] args) {

        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "cc3200");
        System.out.println("111");
        init(system);

    }

    private static void init(ActorSystem<Void> system) {
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServer("192.168.123.131:9092");
        kafkaConfig.setGroupId("1");
        List<String> list = new ArrayList<>();
        list.add("cc3200-1");
        kafkaConfig.setTopics(list);
        system.systemActorOf(CC3200ControlActor.create(kafkaConfig), "ccServer3200", Props.empty());
    }
}
