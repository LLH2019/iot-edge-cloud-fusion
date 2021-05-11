package edge.global;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import base.model.bean.BasicCommon;
import edge.connect.EdgeMqttConnectIn;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/5/9 21:51
 * @description：全局actorRef
 */
public class GlobalAkkaPara {
    public final static Map<String, ActorRef<BasicCommon>> globalActorRefMap = new HashMap<>();
    public final static ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "bootstrap");
    public static EdgeMqttConnectIn mqttConnect = null;
}
