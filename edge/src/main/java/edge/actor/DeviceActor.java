package edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;
import base.model.connect.KafkaConnectOut;
import base.model.connect.MqttConnectIn;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.MqttConfig;
import base.model.connect.bean.MqttInMsg;
import base.type.TopicKey;
import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：接受MQTT接入消息，Kafka发布消息的抽象actor类
 */

public class DeviceActor extends AbstractDeviceActor {
    private static Logger logger = Logger.getLogger(DeviceActor.class.getName());
    public DeviceActor(ActorContext<BasicCommon> context, DeviceModel deviceModel) {
        super(context);
        logger.log(Level.INFO,"DeviceActor pre init...");
        this.mqttConfig = deviceModel.getMqttConfig();
        this.deviceModel = deviceModel;
        this.ref = context.getSelf();
        downConnectIn();
        upConnectOut();
        logger.log(Level.INFO,"DeviceActor init...");
    }

    private MqttConfig mqttConfig;
//    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;
    private KafkaConnectOut kafkaConnectOut;
    private DeviceModel deviceModel;

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

    public static Behavior<BasicCommon> create(DeviceModel deviceModel) {
        return Behaviors.setup(context -> new DeviceActor(context,deviceModel));
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

        KafkaMsg kafkaMsg = new KafkaMsg();
        kafkaMsg.setTopic("cloud.cc3200.1111");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = df.format(new Date());
        kafkaMsg.setKey(key);
        kafkaMsg.setValue(msg.getMsg());
//        System.out.println("999" + kafkaMsg);
        logger.log(Level.INFO, "DeviceActor " + kafkaMsg);
//        System.out.println("handleMqttMsg: " + msg.getMsg());

//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String key = df.format(new Date()) + "-temperature";
        kafkaConnectOut.sendMessageForgetResult(kafkaMsg);
//        sendToKafka(msg);
//        kafkaConnectOut.s
    }
}
