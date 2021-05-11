package edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import edge.connect.EdgeKafkaConnectIn;
import base.model.connect.UpConnectIn;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.SubscribeTopic;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 14:39
 * @description：接收外部消息Actor
 */
public class EdgeKafkaConnectInActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {
    private static Logger logger = Logger.getLogger(EdgeKafkaConnectInActor.class.getName());
    private final Map<String, ActorRef<BasicCommon>> subscribesRefMap = new HashMap<>();
    private final ActorRef<BasicCommon> ref;
    public EdgeKafkaConnectInActor(ActorContext<BasicCommon> context) {
        super(context);
        this.ref = getContext().getSelf();
        new Thread(()->upConnectIn()).start();
        logger.log(Level.INFO, "EdgeKafkaConnectInActor init...");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onHandleKafkaMsgAction)
                .onMessage(SubscribeTopic.class, this::onHandleSubscribeTopic)
                .build();
    }

    private Behavior<BasicCommon> onHandleSubscribeTopic(SubscribeTopic subscribeTopic) {
        String topic = subscribeTopic.getTopic();
        if(topic != null && !"".equals(topic)) {
            subscribesRefMap.put(topic, subscribeTopic.getRef());
        }
        logger.log(Level.INFO, "EdgeKafkaConnectInActor handleSubscribeTopic..." + subscribeTopic);
        return this;
    }


    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
        String topic = msg.getTopic();
        ActorRef<BasicCommon> ref = subscribesRefMap.get(topic);
        if(ref != null) {
            ref.tell(msg);
        }
        logger.log(Level.INFO, "EdgeKafkaConnectInActor pub msg "  + ref);
        return this;
    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new EdgeKafkaConnectInActor(context));
    }

    @Override
    public void upConnectIn() {
        new EdgeKafkaConnectIn(ref);
    }
}
