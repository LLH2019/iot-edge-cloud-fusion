package edge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import base.model.bean.BasicCommon;
import edge.actor.EdgeKafkaConnectInActor;
import edge.actor.EdgeMqttConnectInActor;
import edge.actor.PodActor;
import base.model.connect.bean.KafkaConfig;
import edge.global.GlobalActorRefName;
import edge.global.GlobalAkkaPara;

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
        ActorSystem<Void> system = GlobalAkkaPara.system;
        init(system);
        logger.log(Level.INFO, "EdgeBootstrapMain init...");
    }

    private static void init(ActorSystem<Void> system) {

        ActorRef<BasicCommon> edgeKafkaConnectInActorRef = system.systemActorOf(EdgeKafkaConnectInActor.create(), GlobalActorRefName.EDGE_KAFKA_CONNECT_IN_ACTOR, Props.empty());
        GlobalAkkaPara.globalActorRefMap.put(GlobalActorRefName.EDGE_KAFKA_CONNECT_IN_ACTOR, edgeKafkaConnectInActorRef);
        logger.log(Level.INFO, "init EdgeKafkaConnectInActor..." );

        ActorRef<BasicCommon> edgeMqttConnectInActorRef = system.systemActorOf(EdgeMqttConnectInActor.create(), GlobalActorRefName.EDGE_MQTT_CONNECT_IN_ACTOR, Props.empty());
        GlobalAkkaPara.globalActorRefMap.put(GlobalActorRefName.EDGE_MQTT_CONNECT_IN_ACTOR, edgeMqttConnectInActorRef);
        logger.log(Level.INFO, "init EdgeMqttConnectInActor..." );


        ActorRef<BasicCommon> podActorRef = system.systemActorOf(PodActor.create(), GlobalActorRefName.POD_ACTOR, Props.empty());
        GlobalAkkaPara.globalActorRefMap.put(GlobalActorRefName.POD_ACTOR, podActorRef);
        logger.log(Level.INFO, "init PodActor...");
    }

}
