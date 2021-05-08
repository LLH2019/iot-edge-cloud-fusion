package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.connect.CloudKafkaConnectIn;
import base.model.connect.UpConnectIn;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.SubscribeTopic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 14:39
 * @description：接收外部消息Actor
 */
public class CloudKafkaConnectInActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {
    private Map<String, List<ActorRef<BasicCommon>>> subscribesRefMap = new HashMap<>();
    private ActorRef<BasicCommon> ref;
    private KafkaConfig kafkaConfig;
    private CloudKafkaConnectIn cloudKafkaConnectIn;
    public CloudKafkaConnectInActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.kafkaConfig = kafkaConfig;
        this.ref = getContext().getSelf();
        System.out.println("KafkaConnectInActor--");
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
//        System.out.println("onHandleSubscribeTopic " + subscribeTopic.getTopics().size());
//        System.out.println("onHandleSubscribeTopic-- " + cloudKafkaConnectIn);
//        new Thread(()->kafkaConnectIn.addTopics(subscribeTopic.getTopics()));

        return this;
    }


    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
//        handleMqttMsg(msg);
        String topic = msg.getTopic();
        List<ActorRef<BasicCommon>> refs = subscribesRefMap.get(topic);
        if(refs != null) {
            for (ActorRef<BasicCommon> ref : refs) {
                ref.tell(msg);
            }
        }
        return this;
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new CloudKafkaConnectInActor(context, kafkaConfig));
    }

    @Override
    public void upConnectIn() {
//        System.out.println("888--upConnectIn" + kafkaConfig);
        this.cloudKafkaConnectIn = new CloudKafkaConnectIn(kafkaConfig, ref);
//        System.out.println("888--upConnectIn");
    }
}
