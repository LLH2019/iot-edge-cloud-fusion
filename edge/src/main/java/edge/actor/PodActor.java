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
import edge.global.GlobalActorRefName;
import edge.global.GlobalAkkaPara;

import java.util.List;
/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 21:48
 * @description：某个边缘节点整个的管理actor
 */
public class PodActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {

    private final ActorRef<BasicCommon> ref;
    private final ActorRef<BasicCommon> kafkaConnectInActorRef;

    public PodActor(ActorContext<BasicCommon> context) {
        super(context);
        this.ref = getContext().getSelf();
        this.kafkaConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.EDGE_KAFKA_CONNECT_IN_ACTOR);
        upConnectIn();
    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new PodActor(context));
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .build();
    }

    private Behavior<BasicCommon> onKafkaMsgInAction(KafkaMsg msg) {
        if(TopicKey.CREATE_EDGE_ACTOR.equals(msg.getKey())) {
            DeviceModel model = JSON.parseObject(msg.getValue(), DeviceModel.class);
            String realName = model.getModel().getName() + "-" + model.getModel().getNo();
            getContext().spawn(DeviceActor.create(model), realName);
        }
        System.out.println("kafka-msg: " +msg);
        return this;
    }

    @Override
    public void upConnectIn() {
        SubscribeTopic subscribeTopic = new SubscribeTopic();
        String topic = "edge.edge-pod-1";
        subscribeTopic.setRef(ref);
        subscribeTopic.setTopic(topic);
        kafkaConnectInActorRef.tell(subscribeTopic);
    }
}
