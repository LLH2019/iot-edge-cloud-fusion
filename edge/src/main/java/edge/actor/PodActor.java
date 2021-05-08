package edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.type.TopicKey;
import com.alibaba.fastjson.JSON;
import base.model.bean.DeviceModel;
import base.model.bean.BasicCommon;
import base.model.connect.UpConnectIn;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.SubscribeTopic;

import java.util.List;
/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 21:48
 * @description：某个边缘节点整个的管理actor
 */
public class PodActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {

    private ActorRef<BasicCommon> ref;
    private ActorRef<BasicCommon> kafkaConnectInActorRef;
    private KafkaConfig kafkaConfig;
//    private List<String> subscribeTopics;

    public PodActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.ref = getContext().getSelf();
//        this.subscribeTopics = subscribeTopics;
        this.kafkaConfig = kafkaConfig;
        this.kafkaConnectInActorRef = getContext().spawn(EdgeKafkaConnectInActor.create(kafkaConfig), "up-connect-in");

        upConnectIn();
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new PodActor(context, kafkaConfig));
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
            getContext().spawn(DeviceActor.create(model), realName);
        }
        System.out.println("kafka-msg: " +msg);
        return this;
    }

    @Override
    public void upConnectIn() {
        SubscribeTopic subscribeTopic = new SubscribeTopic();
        String topic = "/edge/" + kafkaConfig.getTopic();
        subscribeTopic.setRef(ref);
        subscribeTopic.setTopic(topic);
//        subscribeTopic.setTopics(subscribeTopics);
        kafkaConnectInActorRef.tell(subscribeTopic);
//        SubscribeTopic topic = new SubscribeTopic();
//        topic.setTopics(this.subscribeTopics);
////        topics.add()
//        kafkaConnectInActorRef.tell(topic);
    }
}
