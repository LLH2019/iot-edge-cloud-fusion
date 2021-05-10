package edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;
import base.model.connect.KafkaConnectOut;
import base.model.connect.bean.SubscribeTopic;
import com.sandinh.paho.akka.MqttPubSub;
import edge.connect.EdgeMqttConnectIn;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.MqttConfig;
import base.model.connect.bean.MqttInMsg;
import edge.global.GlobalActorRefName;
import edge.global.GlobalAkkaPara;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;

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

    private final ActorRef<BasicCommon> ref;
    private KafkaConnectOut kafkaConnectOut;
    private final ActorRef<BasicCommon> edgeMqttConnectInActorRef;
    private final DeviceModel deviceModel;
    private final String realName;

    public DeviceActor(ActorContext<BasicCommon> context, DeviceModel deviceModel) {
        super(context);
        logger.log(Level.INFO,"DeviceActor pre init...");
        this.deviceModel = deviceModel;
        this.realName = deviceModel.getModel().getName() + "-" + deviceModel.getModel().getNo();
        this.ref = context.getSelf();
        this.edgeMqttConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.EDGE_MQTT_CONNECT_IN_ACTOR);
        downConnectIn();
        upConnectOut();
        logger.log(Level.INFO,"DeviceActor init...");
    }

    @Override
    public void downConnectIn() {
        SubscribeTopic sub = new SubscribeTopic();
        sub.setTopic(realName);
        sub.setRef(ref);
        edgeMqttConnectInActorRef.tell(sub);

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
                .onMessage(KafkaMsg.class, this::onHandleKafkaMsgAction)
                .build();
    }

    private Behavior<BasicCommon> onHandleKafkaMsgAction(KafkaMsg msg) {
        logger.log(Level.INFO, "DeviceActor onHandleKafkaMsgAction " + msg);
        return this;
    }

    private Behavior<BasicCommon> onMqttMsgInAction(MqttInMsg msg) {
        handleMqttMsg(msg);
        return this;
    }

    public void handleMqttMsg(MqttInMsg msg) {
//        logger.log(Level.INFO, "mqtt ");
        KafkaMsg kafkaMsg = new KafkaMsg();
        kafkaMsg.setTopic("cloud." + deviceModel.getModel().getName() + "."+ deviceModel.getModel().getNo());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = df.format(new Date());
        kafkaMsg.setKey(key);
        kafkaMsg.setValue(msg.getMsg());
        logger.log(Level.INFO, "DeviceActor " + kafkaMsg);
        kafkaConnectOut.sendMessageForgetResult(kafkaMsg);
    }
}
