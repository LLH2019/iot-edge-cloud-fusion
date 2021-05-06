package things.brain.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import things.base.DataType;
import things.brain.bean.NewDeviceConn;
import things.brain.bean.QueryMongoDBData;
import things.controller.actor.CloudControlActor;
import things.edge.actor.KafkaConnectInActor;
import things.model.bean.BasicCommon;
import things.model.bean.DeviceModel;
import things.model.connect.UpConnectIn;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.SubscribeTopic;

import java.util.List;
import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:01
 * @description：最高控制的actor
 */
public class BrainControlActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {

    private ActorRef<BasicCommon> kafkaConnectInActorRef;
    private Map<String, ActorRef<BasicCommon>> cloudControlRefMaps;
    private List<String> subscribeTopics;

    public BrainControlActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.kafkaConnectInActorRef = getContext().spawn(KafkaConnectInActor.create(kafkaConfig), "kafka-connect-in");
//        cloudControlRefMaps.put()
        upConnectIn();
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
        ActorRef<BasicCommon>  ref = getContext().spawn(CloudControlActor.create(model), model.getRealName());
        cloudControlRefMaps.put(model.getRealName(), ref);

        SubscribeTopic subscribeTopic = new SubscribeTopic();
        subscribeTopic.setTopics(model.getTopics());
        subscribeTopic.setRef(ref);
        kafkaConnectInActorRef.tell(subscribeTopic);
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
