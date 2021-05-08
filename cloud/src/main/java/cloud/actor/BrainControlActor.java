package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.type.DataType;
import cloud.bean.NewDeviceConn;
import cloud.bean.QueryMongoDBData;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;
import base.model.connect.UpConnectIn;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.SubscribeTopic;

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
    private ActorRef<BasicCommon> kafkaConnectInActorRef;
    private Map<String, ActorRef<BasicCommon>> cloudControlRefMaps;
//    private List<String> subscribeTopics;

    public BrainControlActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.kafkaConnectInActorRef = getContext().spawn(CloudKafkaConnectInActor.create(kafkaConfig), "cloud-kafka-connect-in");
        logger.log(Level.INFO, "BrainControlActor init...");

        //        cloudControlRefMaps.put()
//        upConnectIn();
    }


    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
//                .onMessage(NewDeviceConn.class, this::onNewDeviceConnAction)
//                .onMessage(QueryMongoDBData.class, this::onHandleMongoDBAction)
                .onMessage(DeviceModel.class, this::onHandleDeviceLink)
                .build();
    }

    private Behavior<BasicCommon> onHandleDeviceLink(DeviceModel model) {
        logger.log(Level.INFO, "BrainControlActor handle device link..." + model);

        String realName = model.getModel().getName() + "-" +  model.getModel().getNo();
        logger.log(Level.INFO, "BrainControlActor spawn device..." + realName + model);
        ActorRef<BasicCommon>  ref = getContext().spawn(DeviceCloudActor.create(model), realName);
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

    private Behavior<BasicCommon> onHandleMongoDBAction(QueryMongoDBData data) {

        if(DataType.NEW_MODEl.equals(data.getType())) {
            System.out.println(data.getDoc());
        }

        return this;
    }

    private Behavior<BasicCommon> onNewDeviceConnAction(NewDeviceConn conn) {
//        ActorRef<BasicCommon>  ref = getContext().spawn(CloudControlActor.create(conn.getKafkaConfig()), conn.getName());
////        ref.tell()
//        refMaps.put(conn.getName(), ref);

        return this;
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new BrainControlActor(context, kafkaConfig));
    }

    @Override
    public void upConnectIn() {
//        SubscribeTopic topic = new SubscribeTopic();
////        topic.setTopics(this.topics);
////        topics.add()
//        kafkaConnectInActorRef.tell(topic);
    }
}
