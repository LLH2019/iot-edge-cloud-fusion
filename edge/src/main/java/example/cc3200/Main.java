package example.cc3200;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import example.cc3200.actor.CC3200Actor;
import example.cc3200.actor.MqttActor;
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
        MqttConfig config = new MqttConfig();
        config.topic = "cc3200/humidity";
        config.brokerUrl = "tcp://192.168.123.247:1883";
        config.clientId ="123456";
        ActorRef<Command> ref = system.systemActorOf(CC3200Actor.create(null), "232", Props.empty());
        ActorSystem.create(MqttActor.create(config, ref), "222");

    }
}
