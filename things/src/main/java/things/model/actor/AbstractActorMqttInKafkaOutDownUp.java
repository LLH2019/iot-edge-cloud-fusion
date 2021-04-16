package things.model.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import things.model.bean.BasicCommon;
import things.model.connect.KafkaConnectOut;
import things.model.connect.MqttConnectIn;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.MqttConfig;
import things.model.connect.bean.MqttInMsg;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：接受MQTT接入消息，Kafka发布消息的抽象actor类
 */

public class AbstractActorMqttInKafkaOutDownUp extends AbstractBasicActor {
    public AbstractActorMqttInKafkaOutDownUp(ActorContext<BasicCommon> context, MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        super(context);
        System.out.println("AbstractActorMqttInKafkaOutDownUp...");
        this.mqttConfig = mqttConfig;
        this.kafkaConfig = kafkaConfig;
        this.ref = context.getSelf();
        downConnectIn();
        upConnectOut();
    }

    private MqttConfig mqttConfig;
    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;
    private KafkaConnectOut kafkaConnectOut;

    public KafkaConnectOut getKafkaConnectOut() {
        return kafkaConnectOut;
    }

    @Override
    public void downConnectIn() {
        new MqttConnectIn(mqttConfig, ref);
    }

    @Override
    public void upConnectOut() {
        this.kafkaConnectOut = new KafkaConnectOut(kafkaConfig);
    }

    public static Behavior<BasicCommon> create(MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new AbstractActorMqttInKafkaOutDownUp(context, mqttConfig, kafkaConfig));
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(MqttInMsg.class, this::onMqttMsgInAction)


                .build();
    }

    private Behavior<BasicCommon> onMqttMsgInAction(MqttInMsg msg) {
        handleMqttMsg(msg);
        return this;
    }

    public void handleMqttMsg(MqttInMsg msg) {
//        kafkaConnectOut.s
    }
}
