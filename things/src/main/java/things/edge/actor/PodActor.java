package things.edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.alibaba.fastjson.JSON;
import things.base.TopicKey;
import things.model.actor.AbstractActorMqttInKafkaOutDownUp;
import things.model.bean.AbstractModel;
import things.model.bean.BasicCommon;
import things.model.connect.KafkaConnectIn;
import things.model.connect.UpConnectIn;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 21:48
 * @description：某个边缘节点整个的管理actor
 */
public class PodActor extends AbstractBehavior<BasicCommon> implements UpConnectIn {

    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;

    public PodActor(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.ref = getContext().getSelf();
        this.kafkaConfig = kafkaConfig;
        new Thread(){
            @Override
            public void run() {
                upConnectIn();
            }
        }.start();
    }

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
//        System.out.println("create");
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
            AbstractModel model = JSON.parseObject(msg.getValue(), AbstractModel.class);
            getContext().spawn(AbstractActorMqttInKafkaOutDownUp.create(model.getMqttConfig(), model.getKafkaConfig()), model.getName());
        }
        System.out.println("kafka-msg: " +msg);
        return this;
    }

    @Override
    public void upConnectIn() {
        new KafkaConnectIn(kafkaConfig, ref);
    }
}
