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
import things.model.actor.AbstractActorMqttInKafkaOutDownUp;
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

    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;
    private ActorRef<BasicCommon> upConnectInActorRef;
    private List<String> topics;

    public PodActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig, List<String> topics) {
        super(context);
        this.ref = getContext().getSelf();
        this.kafkaConfig = kafkaConfig;
        this.topics = topics;
        this.upConnectInActorRef = getContext().spawn(UpConnectActor.create(kafkaConfig), "up-connect-in");
        upConnectIn();
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig, List<String> topics) {
//        System.out.println("create");
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
            CreateEdgeActorMsg createEdgeActorMsg = JSON.parseObject(msg.getValue(), CreateEdgeActorMsg.class);
            DeviceModel model = createEdgeActorMsg.getModel();
            System.out.println("222 " + model);
            getContext().spawn(AbstractActorMqttInKafkaOutDownUp.create(model.getMqttConfig(), model.getKafkaConfig()), model.getName());
        }
        System.out.println("kafka-msg: " +msg);
        return this;
    }

    @Override
    public void upConnectIn() {
        SubscribeTopic topic = new SubscribeTopic();
        topic.setTopics(this.topics);
//        topics.add()
        upConnectInActorRef.tell(topic);
    }
}
