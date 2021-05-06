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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：接受MQTT接入消息，Kafka发布消息的抽象actor类
 */

public class DeviceActor extends AbstractDeviceActor {
    public DeviceActor(ActorContext<BasicCommon> context, MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        super(context);
        System.out.println("AbstractActorMqttInKafkaOutDownUp...");
        this.mqttConfig = mqttConfig;
        this.ref = context.getSelf();
        downConnectIn();
        upConnectOut();
    }

    private MqttConfig mqttConfig;
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
        this.kafkaConnectOut = new KafkaConnectOut();
    }

    public static Behavior<BasicCommon> create(MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new DeviceActor(context, mqttConfig, kafkaConfig));
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
//        MessageHandler handler = new MessageHandler();
//        Message message = handler.handleMqttUpMsg(msg.getMsg());
        System.out.println("handleMqttMsg: " + msg.getMsg());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = df.format(new Date()) + "-temperature";
//        getKafkaConnectOut().sendMessageForgetResult(kafkaConfig.getTopics().get(0), key, msg.getMsg());
//        sendToKafka(msg);
//        kafkaConnectOut.s
    }
}
