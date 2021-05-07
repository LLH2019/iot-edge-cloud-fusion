package things.edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.alibaba.fastjson.JSON;
import things.base.TopicKey;
import things.brain.bean.CreateEdgeActorMsg;
import things.model.actor.DeviceActor;
import things.model.bean.DeviceModel;
import things.model.bean.BasicCommon;
import things.model.connect.UpConnectIn;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;
import things.model.connect.bean.SubscribeTopic;

import java.util.List;
/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 21:48
 * @description：某个边缘节点整个的管理actor
 */
public class PodActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {

    private ActorRef<BasicCommon> ref;
    private ActorRef<BasicCommon> kafkaConnectInActorRef;
    private List<String> subscribeTopics;

    public PodActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig, List<String> subscribeTopics) {
        super(context);
        this.ref = getContext().getSelf();
        this.subscribeTopics = subscribeTopics;
        this.kafkaConnectInActorRef = getContext().spawn(KafkaConnectInActor.create(kafkaConfig), "up-connect-in");

        upConnectIn();
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig, List<String> topics) {
        return Behaviors.setup(context -> new PodActor(context, kafkaConfig, topics));
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .build();
    }

    private Behavior<BasicCommon> onKafkaMsgInAction(KafkaMsg msg) {
        if(TopicKey.CREATE_EDGE_ACTOR.equals(msg.getKey())) {
            System.out.println("111111");
            DeviceModel model = JSON.parseObject(msg.getValue(), DeviceModel.class);
            System.out.println("222 " + model);
            String realName = model.getModel().getName() + "-" + model.getModel().getNo();
            getContext().spawn(DeviceActor.create(model.getMqttConfig(), model.getKafkaConfig()), realName);
        }
        System.out.println("kafka-msg: " +msg);
        return this;
    }

    @Override
    public void upConnectIn() {
        SubscribeTopic subscribeTopic = new SubscribeTopic();
        subscribeTopic.setRef(ref);
        subscribeTopic.setTopics(subscribeTopics);
        kafkaConnectInActorRef.tell(subscribeTopic);
//        SubscribeTopic topic = new SubscribeTopic();
//        topic.setTopics(this.subscribeTopics);
////        topics.add()
//        kafkaConnectInActorRef.tell(topic);
    }
}
