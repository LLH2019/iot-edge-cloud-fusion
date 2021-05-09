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
import cloud.front.DeviceInfo;
import cloud.front.GetKafkaMsg;
import cloud.front.TotalInfo;
import cloud.global.GlobalAkkaPara;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 14:39
 * @description：接收外部消息Actor
 */
public class CloudKafkaConnectInActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {
    private static Logger logger = Logger.getLogger(CloudKafkaConnectInActor.class.getName());

    private final ActorSystem<?> system;
    private final Map<String, ActorRef<BasicCommon>> subscribesRefMap = new HashMap<>();
    private final ActorRef<BasicCommon> ref;
    public CloudKafkaConnectInActor(ActorContext<BasicCommon> context) {
        super(context);
        this.system = GlobalAkkaPara.system;
        this.ref = getContext().getSelf();
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
        subscribesRefMap.put(topic, subscribeTopic.getRef());

        logger.log(Level.INFO, "CloudKafkaConnectInActor handleSubscribeTopic...");
        return this;
    }


    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
//        System.out.println("666666");
        logger.log(Level.INFO, "6666CloudKafkaConnectInActor " + msg );
//        GetKafkaMsg.kafkaMsg = msg;
////        TotalInfo.deviceNums =
//        if(!topics.contains(msg.getTopic())) {
//            TotalInfo.deviceNums = topics.size();
//            TotalInfo.deviceSets.add(msg.getTopic());
//            DeviceInfo deviceInfo = new DeviceInfo();
//            deviceInfo.setName(msg.getTopic());
//            Map<String, String> valueMap = new HashMap<>();
//            String[] strs = msg.getValue().split(":");
//            valueMap.put(strs[0], strs[1]);
//            deviceInfo.setValues(valueMap);
//            TotalInfo.deviceInfoMap.put(msg.getTopic(), deviceInfo);
//        } else {
//            DeviceInfo deviceInfo = TotalInfo.deviceInfoMap.get(msg.getKey());
//            Map<String, String> valueMap = deviceInfo.getValues();
//            String[] strs = msg.getValue().split(":");
//            valueMap.put(strs[0], strs[1]);
//            deviceInfo.setValues(valueMap);
//        }


//        handleMqttMsg(msg);
//        String topic = msg.getTopic();
//        ActorRef<BasicCommon> ref = subscribesRefMap.get(topic);
//        System.out.println("555" + ref);
//        if(ref != null) {
//            ref.tell(msg);
//        }
        return this;
    }

//    private void handleMqttMsg(KafkaMsg msg) {
//        logger.log(Level.INFO, "CloudKafkaConnectInActor " + msg );
//    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new CloudKafkaConnectInActor(context));
    }

    @Override
    public void upConnectIn() {
//        System.out.println("888--upConnectIn" + kafkaConfig);
//        this.cloudKafkaConnectIn = new CloudKafkaConnectIn(kafkaConfig, ref);
//        System.out.println("888--upConnectIn");
        new CloudKafkaConsumer(system, ref);
    }
}
