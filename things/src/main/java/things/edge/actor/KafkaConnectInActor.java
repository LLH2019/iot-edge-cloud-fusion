package things.edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import things.model.bean.BasicCommon;
import things.model.connect.KafkaConnectIn;
import things.model.connect.UpConnectIn;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;
import things.model.connect.bean.SubscribeTopic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 14:39
 * @description：接收外部消息Actor
 */
public class KafkaConnectInActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {
    private Map<String, List<ActorRef<BasicCommon>>> subscribesRefMap = new HashMap<>();
    private ActorRef<BasicCommon> ref;
    private KafkaConfig kafkaConfig;
    private KafkaConnectIn kafkaConnectIn;
    public KafkaConnectInActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.kafkaConfig = kafkaConfig;
        this.ref = getContext().getSelf();
        new Thread(()->upConnectIn()).start();
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onHandleKafkaMsgAction)
                .onMessage(SubscribeTopic.class, this::onHandleSubscribeTopic)
                .build();
    }

    private Behavior<BasicCommon> onHandleSubscribeTopic(SubscribeTopic subscribeTopic) {
        List<String> topics = subscribeTopic.getTopics();
        if(topics != null) {
            for (String topic : topics) {
                if (!subscribesRefMap.containsKey(topic)) {
                    List<ActorRef<BasicCommon>> list = new ArrayList<>();
                    list.add(subscribeTopic.getRef());
                    subscribesRefMap.put(topic, list);
                } else {
                    List<ActorRef<BasicCommon>> list = subscribesRefMap.get(topic);
                    list.add(subscribeTopic.getRef());
                    subscribesRefMap.put(topic, list);
                }
            }
            System.out.println("onHandleSubscribeTopic...");
        }

        kafkaConnectIn.addTopics(subscribeTopic.getTopics());
        return this;
    }


    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
//        handleMqttMsg(msg);
        String topic = msg.getTopic();
        List<ActorRef<BasicCommon>> refs = subscribesRefMap.get(topic);
        for(ActorRef<BasicCommon> ref : refs) {
            ref.tell(msg);
        }
        return this;
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new KafkaConnectInActor(context, kafkaConfig));
    }

    @Override
    public void upConnectIn() {
        this.kafkaConnectIn = new KafkaConnectIn(kafkaConfig, ref);
    }
}
