package things.model.actor;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import things.model.bean.BasicCommon;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.MqttConfig;
import things.model.connect.bean.MqttInMsg;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：CC3200 actor类
 */
public class CC3200Actor extends AbstractActorMqttInKafkaOutDownUp {

    public CC3200Actor(ActorContext<BasicCommon> context, MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        super(context, mqttConfig, kafkaConfig);
        System.out.println("CC3200Actor");
    }

    @Override
    public void handleMqttMsg(MqttInMsg msg) {
        System.out.println("handleMqttMsg: " + msg.getMsg());
//        System.out.println(msg.getMsg());
//        super.handleMqttMsg(msg);
    }

    public static Behavior<BasicCommon> create(MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        System.out.println("create");
        return Behaviors.setup(context -> new CC3200Actor(context, mqttConfig, kafkaConfig));
    }


    public static void main(String[] args) {
        ActorSystem<Void> system =ActorSystem.create(Behaviors.empty(), "cc3200");
        System.out.println("111");
        init(system);
    }

    private static void init(ActorSystem<Void> system) {
        MqttConfig mqttConfig1 = new MqttConfig();
        mqttConfig1.setTopic( "cc3200/humidity");
        mqttConfig1.setBrokerUrl("tcp://192.168.123.247:1883");
        mqttConfig1.setClientId("123456");

        KafkaConfig kafkaConfig1 = new KafkaConfig();
        kafkaConfig1.setServer("192.168.123.131:9092");

        system.systemActorOf(CC3200Actor.create(mqttConfig1, kafkaConfig1), "cc3200", Props.empty());
//        CC3200Actor.create(mqttConfig1, kafkaConfig1);
    }
}
