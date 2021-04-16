package things.controller.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import things.model.bean.BasicCommon;
import things.model.connect.KafkaConnectIn;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:33
 * @description：kafka接收消息的控制actor
 */
public class BrainControlActorKafkaInAndOut extends BrainControlActor{

//    private KafkaConnectIn kafkaConnectIn;
    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;

    public BrainControlActorKafkaInAndOut(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.ref = context.getSelf();
        this.kafkaConfig = kafkaConfig;
        downConnectIn();
    }

    @Override
    public void init() {

    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .build();
    }

    private Behavior<BasicCommon> onKafkaMsgInAction(KafkaMsg kafkaMsg) {
        handleKafkaMsg(kafkaMsg);
        return this;
    }

    public void handleKafkaMsg(KafkaMsg kafkaMsg) {

    }

    @Override
    public void downConnectIn() {
        new KafkaConnectIn(kafkaConfig, ref);
    }

    @Override
    public void upConnectOut() {
        super.upConnectOut();
    }



    public static void main(String[] args) {
    }
}
