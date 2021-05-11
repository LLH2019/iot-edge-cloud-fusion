package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import cloud.connect.CloudKafkaConnectIn;
import base.model.connect.UpConnectIn;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.SubscribeTopic;
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
//        new Thread(()->upConnectIn()).start();
        upConnectIn();
        logger.log(Level.INFO, "CloudKafkaConnectInActor init...");
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
        logger.log(Level.INFO, "CloudKafkaConnectInActor handleSubscribeTopic..." + subscribeTopic);
        return this;
    }


    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
        logger.log(Level.INFO, "CloudKafkaConnectInActor onHandleKafkaMsgAction " + msg );
        if("close".equals(msg.getValue())) {
            logger.log(Level.INFO, "DeviceCloudActor : kafka msg content is close...");
        } else {
            if(TotalInfo.deviceInfoMap.containsKey(msg.getTopic())) {
                Map<String, String> propertyMap = TotalInfo.deviceInfoMap.get(msg.getTopic()).getPropertyMap();
                String[] strs = msg.getValue().split(":");
                if (strs.length == 2) {
                    propertyMap.put(strs[0], strs[1]);
                }
            }
        }
        String topic = msg.getTopic();
        ActorRef<BasicCommon> ref = subscribesRefMap.get(topic);
        if(ref != null) {
            ref.tell(msg);
        }
        logger.log(Level.INFO, "CloudKafkaConnectInActor pub msg "  + ref);
        return this;
    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new CloudKafkaConnectInActor(context));
    }

    @Override
    public void upConnectIn() {
//        new CloudKafkaConsumer(system, ref);
        new CloudKafkaConnectIn(ref);
    }
}
