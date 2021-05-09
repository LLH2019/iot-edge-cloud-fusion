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
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.SubscribeTopic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 14:39
 * @description：接收外部消息Actor
 */
public class EdgeKafkaConnectInActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {
    private static Logger logger = Logger.getLogger(EdgeKafkaConnectInActor.class.getName());
    private final Map<String, List<ActorRef<BasicCommon>>> subscribesRefMap = new HashMap<>();
    private final ActorRef<BasicCommon> ref;
//    private final KafkaConfig kafkaConfig;
//    private EdgeKafkaConnectIn edgeKafkaConnectIn;
    public EdgeKafkaConnectInActor(ActorContext<BasicCommon> context) {
        super(context);
//        this.kafkaConfig = kafkaConfig;
        this.ref = getContext().getSelf();
//        System.out.println("KafkaConnectInActor--");
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
        if (topic != null && !"".equals(topic)) {
//            for (String topic : topics) {
            if (!subscribesRefMap.containsKey(topic)) {
                List<ActorRef<BasicCommon>> list = new ArrayList<>();
                list.add(subscribeTopic.getRef());
                subscribesRefMap.put(topic, list);
            } else {
                List<ActorRef<BasicCommon>> list = subscribesRefMap.get(topic);
                list.add(subscribeTopic.getRef());
                subscribesRefMap.put(topic, list);
            }
//            }
            System.out.println("onHandleSubscribeTopic...");
        }
        return this;
    }


    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
//        handleMqttMsg(msg);
        String topic = msg.getTopic();
        logger.log(Level.INFO, "EdgeKafkaConnectInActor " + msg + " " + topic);
        List<ActorRef<BasicCommon>> refs = subscribesRefMap.get(topic);
        for(ActorRef<BasicCommon> ref : refs) {
            ref.tell(msg);
        }
        return this;
    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new EdgeKafkaConnectInActor(context));
    }

    @Override
    public void upConnectIn() {
//        System.out.println("888--upConnectIn" + kafkaConfig);
        new EdgeKafkaConnectIn(ref);
//        System.out.println("888--upConnectIn");
    }
}
