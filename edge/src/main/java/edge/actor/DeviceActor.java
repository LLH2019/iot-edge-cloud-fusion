package edge.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;
import base.model.bean.Event;
import edge.connect.EdgeMqttConnectOut;
import edge.connect.KafkaConnectOut;
import base.model.connect.bean.SubscribeTopic;
import base.type.TopicKey;
import base.model.connect.bean.KafkaMsg;
import base.model.connect.bean.MqttInMsg;
import edge.global.GlobalActorRefName;
import edge.global.GlobalAkkaPara;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
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
    private EdgeMqttConnectOut mqttConnectOut;
    private final ActorRef<BasicCommon> edgeMqttConnectInActorRef;
    private final ActorRef<BasicCommon> edgeKafkaConnectInActorRef;
    private final DeviceModel deviceModel;
    private final String realName;
    private final Set<String> eventSets = new HashSet<>();

    public DeviceActor(ActorContext<BasicCommon> context, DeviceModel deviceModel) {
        super(context);
        logger.log(Level.INFO,"DeviceActor pre init...");
        this.deviceModel = deviceModel;
        this.realName = deviceModel.getModel().getName() + "-" + deviceModel.getModel().getNo();
        this.ref = context.getSelf();
        this.edgeMqttConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.EDGE_MQTT_CONNECT_IN_ACTOR);
        this.edgeKafkaConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.EDGE_KAFKA_CONNECT_IN_ACTOR);
        initEventSet();
        downConnectIn();
        downConnectOut();
        upConnectOut();
        upConnectIn();
        logger.log(Level.INFO,"DeviceActor init...");
    }

    private void initEventSet() {
        List<Event> eventList = deviceModel.getModel().getEvents();
        for (Event e : eventList) {
            eventSets.add(e.getName());
        }
    }

    @Override
    public void downConnectOut() {
        this.mqttConnectOut = new EdgeMqttConnectOut();
    }

    @Override
    public void upConnectIn() {
        SubscribeTopic subscribeTopic = new SubscribeTopic();
        String topic = "edge." + deviceModel.getModel().getName() + "." + deviceModel.getModel().getNo();
        subscribeTopic.setTopic(topic);
        subscribeTopic.setRef(ref);
        edgeKafkaConnectInActorRef.tell(subscribeTopic);
    }

    @Override
    public void downConnectIn() {
        SubscribeTopic sub = new SubscribeTopic();
        sub.setTopic(realName);
        sub.setRef(ref);
        logger.log(Level.INFO, "device actor link to mqtt " + realName + ref);
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
        if(TopicKey.CONTROL_DEVICE.equals(msg.getKey()) && eventSets.contains(msg.getValue())) {
            String pubTopic = "device/down/" + deviceModel.getModel().getName() + "/" + deviceModel.getModel().getNo();
            mqttConnectOut.publishMessage(pubTopic, msg.getValue());
            logger.log(Level.INFO, "success send to msg to mqtt" + pubTopic + " " +msg);
        }

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
        logger.log(Level.INFO, "DeviceActor send msg to kafka " + kafkaMsg);
        kafkaConnectOut.sendMessageForgetResult(kafkaMsg);
    }
}
