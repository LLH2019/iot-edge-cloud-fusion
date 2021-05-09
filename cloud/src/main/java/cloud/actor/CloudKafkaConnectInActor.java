package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import cloud.bean.KafkaMsgList;
import cloud.connect.CloudKafkaConnectIn;
import base.model.connect.UpConnectIn;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.SubscribeTopic;
import cloud.connect.CloudKafkaConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 14:39
 * @description：接收外部消息Actor
 */
public class CloudKafkaConnectInActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {
    private static Logger logger = Logger.getLogger(CloudKafkaConnectInActor.class.getName());

    private ActorSystem<?> system;
    private Map<String, ActorRef<BasicCommon>> subscribesRefMap = new HashMap<>();
    private ActorRef<BasicCommon> ref;
    private KafkaConfig kafkaConfig;
    private CloudKafkaConnectIn cloudKafkaConnectIn;
//    private CloudKafkaConsumer cloudKafkaConsumer;
    public CloudKafkaConnectInActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig, ActorSystem<?> system) {
        super(context);
        this.kafkaConfig = kafkaConfig;
        this.system = system;
        this.ref = getContext().getSelf();
//        System.out.println("KafkaConnectInActor--");
        new Thread(()->upConnectIn()).start();

        logger.log(Level.WARNING, "CloudKafkaConnectInActor init...");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onHandleKafkaMsgAction)
                .onMessage(SubscribeTopic.class, this::onHandleSubscribeTopic)
                .onMessage(KafkaMsgList.class, this::onHandleKafkaMsgListAction)
                .build();
    }

    private Behavior<BasicCommon> onHandleKafkaMsgListAction(KafkaMsgList kafkaMsgList) {
        System.out.println(kafkaMsgList);

        return this;
    }

    private Behavior<BasicCommon> onHandleSubscribeTopic(SubscribeTopic subscribeTopic) {
        String topic = subscribeTopic.getTopic();
//            for (String topic : topics) {

        subscribesRefMap.put(topic, subscribeTopic.getRef());

//
        logger.log(Level.INFO, "CloudKafkaConnectInActor handleSubscribeTopic...");
//        System.out.println("onHandleSubscribeTopic " + subscribeTopic.getTopics().size());
//        System.out.println("onHandleSubscribeTopic-- " + cloudKafkaConnectIn);
//        new Thread(()->kafkaConnectIn.addTopics(subscribeTopic.getTopics()));

        return this;
    }


    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
        System.out.println("666666");
        logger.log(Level.INFO, "CloudKafkaConnectInActor " + msg );
        handleMqttMsg(msg);
//        String topic = msg.getTopic();
//        ActorRef<BasicCommon> ref = subscribesRefMap.get(topic);
//        System.out.println("555" + ref);
//        if(ref != null) {
//            ref.tell(msg);
//        }
        return this;
    }

    private void handleMqttMsg(KafkaMsg msg) {
        logger.log(Level.INFO, "CloudKafkaConnectInActor " + msg );
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig, ActorSystem<?> system) {
        return Behaviors.setup(context -> new CloudKafkaConnectInActor(context, kafkaConfig,system));
    }

    @Override
    public void upConnectIn() {
//        System.out.println("888--upConnectIn" + kafkaConfig);
        this.cloudKafkaConnectIn = new CloudKafkaConnectIn(kafkaConfig, ref);
//        System.out.println("888--upConnectIn");
//        this.cloudKafkaConsumer = new CloudKafkaConsumer(system, ref);
    }
}
