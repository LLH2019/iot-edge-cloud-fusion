package example.cc3200;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import example.cc3200.actor.CC3200Actor;
import example.cc3200.actor.MqttActor;
import example.cc3200.actor.MqttAgent;
import example.cc3200.bean.CC3200Desc;
import example.cc3200.bean.Command;
import example.cc3200.bean.MqttConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ActorSystem<Void> system =ActorSystem.create(Behaviors.empty(), "cc3200");
        System.out.println("111");
        init(system);
    }

    private static void init(ActorSystem<Void> system) {

        ActorRef<MqttAgent.Command> agentRef =system.systemActorOf(MqttAgent.create(), "mqttAgent", Props.empty());

        MqttConfig config1 = new MqttConfig();
        config1.topic = "cc3200/humidity";
        config1.brokerUrl = "tcp://192.168.123.247:1883";
        config1.clientId ="123456";
        CC3200Desc desc1 = new CC3200Desc();
        desc1.setName("cc3200-1");
        desc1.setPos("lib-1");
        ActorRef<Command> CC3200Ref = system.systemActorOf(CC3200Actor.create(desc1), "CC3200-1", Props.empty());

        ActorRef<MqttActor.Command> mqttActorRef = system.systemActorOf(MqttActor.create(config1, CC3200Ref), "MqttActor-1", Props.empty());
        MqttAgent.AddMqttActor addMqttActor = new MqttAgent.AddMqttActor("MqttActor-1", mqttActorRef);

        agentRef.tell(addMqttActor);


//        MqttConfig config2 = new MqttConfig();
//        config2.topic = "cc3200-2/temperature";
//        config2.brokerUrl = "tcp://192.168.123.247:1883";
//        config2.clientId ="123457";
//        CC3200Desc desc2 = new CC3200Desc();
//        desc2.setName("cc3200-2");
//        desc2.setPos("lib-2");
//        ActorRef<Command> CC3200Ref2 = system.systemActorOf(CC3200Actor.create(desc2), "CC3200-2", Props.empty());
//        ActorRef<MqttActor.Command> mqttActorRef2 = system.systemActorOf(MqttActor.create(config2, CC3200Ref2), "MqttActor-2", Props.empty());
//        MqttAgent.AddMqttActor addMqttActor2 = new MqttAgent.AddMqttActor("MqttActor-2", mqttActorRef2);
//
//        agentRef.tell(addMqttActor2);


    }
}
