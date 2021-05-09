package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.connect.bean.KafkaMsg;
import base.type.DataType;
import cloud.front.GetKafkaMsg;
import cloud.bean.NewDeviceConn;
import cloud.bean.QueryMongoDBData;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;
import base.model.connect.UpConnectIn;
import base.model.connect.bean.KafkaConfig;
import cloud.connect.CloudKafkaConsumer;
import cloud.global.GlobalActorRefName;
import cloud.global.GlobalAkkaPara;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:01
 * @description：最高控制的actor
 */
public class BrainControlActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {

    private static Logger logger = Logger.getLogger(BrainControlActor.class.getName());
    private final ActorRef<BasicCommon> kafkaConnectInActorRef;
    private final Map<String, ActorRef<BasicCommon>> cloudControlRefMaps = new HashMap<>();
    private final ActorSystem<?> system;

    public BrainControlActor(ActorContext<BasicCommon> context) {
        super(context);
        this.system = GlobalAkkaPara.system;
        this.kafkaConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.CLOUD_KAFKA_CONNECT_IN_ACTOR);
        logger.log(Level.INFO, "BrainControlActor init...");

//        new Thread(()->upConnectIn()).start();
    }


    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onHandleKafkaMsg)
                .onMessage(DeviceModel.class, this::onHandleDeviceLink)
                .build();
    }

    private Behavior<BasicCommon> onHandleKafkaMsg(KafkaMsg msg) {
        GetKafkaMsg.kafkaMsg = msg;
        System.out.println("5555 " + msg);
        return this;
    }

    private Behavior<BasicCommon> onHandleDeviceLink(DeviceModel model) {
        logger.log(Level.INFO, "BrainControlActor handle device link..." + model);

        String realName = model.getModel().getName() + "-" +  model.getModel().getNo();
        logger.log(Level.INFO, "BrainControlActor spawn device..." + realName + model);
        ActorRef<BasicCommon>  ref = getContext().spawn(DeviceCloudActor.create(model, kafkaConnectInActorRef), realName);
        cloudControlRefMaps.put(realName, ref);
//
//        SubscribeTopic subscribeTopic = new SubscribeTopic();
////        subscribeTopic.setTopics(model.getKafkaConfig().getTopic());
//        String topic = "/cloud/" + model.getModel().getName() + "/" + model.getModel().getNo();
//        subscribeTopic.setTopic(topic);
//        subscribeTopic.setRef(ref);
//        kafkaConnectInActorRef.tell(subscribeTopic);
        return this;
    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new BrainControlActor(context));
    }

    @Override
    public void upConnectIn() {
        System.out.println("00000000");
        new CloudKafkaConsumer(system, getContext().getSelf());
    }
}
