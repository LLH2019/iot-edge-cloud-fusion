package edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.connect.DownConnectIn;
import base.model.connect.bean.MqttInMsg;
import base.model.connect.bean.SubscribeTopic;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/5/10 10:56
 * @description：边缘端mqtt接入actor
 */
public class EdgeMqttConnectInActor extends AbstractBehavior<BasicCommon> {
    private static Logger logger = Logger.getLogger(EdgeMqttConnectInActor.class.getName());
    private final Map<String, ActorRef<BasicCommon>> subscribesRefMap = new HashMap<>();
    private final ActorRef<BasicCommon> ref;

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new EdgeMqttConnectInActor(context));
    }

    public EdgeMqttConnectInActor(ActorContext<BasicCommon> context) {
        super(context);
        this.ref = context.getSelf();
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(SubscribeTopic.class, this::onHandleSubscribeTopic)
                .onMessage(MqttInMsg.class, this::onHandleMqttInMsg)
                .build();
    }

    private Behavior<BasicCommon> onHandleMqttInMsg(MqttInMsg msg) {
        String [] strs = msg.getTopic().split("/");
        String topic =  strs[2] + "-" + strs[3];
        ActorRef<BasicCommon> deviceActorRef = subscribesRefMap.get(topic);
        if(null != deviceActorRef) {
            deviceActorRef.tell(msg);
        }
        return this;
    }

    private Behavior<BasicCommon> onHandleSubscribeTopic(SubscribeTopic sub) {
        subscribesRefMap.put(sub.getTopic(), sub.getRef());
        return this;
    }

}
