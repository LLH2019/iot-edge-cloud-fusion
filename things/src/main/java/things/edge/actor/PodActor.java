package things.edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
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
        this.ref = context.getSelf();
        this.kafkaConfig = kafkaConfig;
        upConnectIn();
    }




    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .build();
    }

    private Behavior<BasicCommon> onKafkaMsgInAction(KafkaMsg msg) {
        return this;
    }

    @Override
    public void upConnectIn() {
        new KafkaConnectIn(kafkaConfig, ref);
    }
}
