package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.SubscribeTopic;
import base.type.TopicKey;
import com.alibaba.fastjson.JSON;
import base.model.bean.DeviceModel;
import base.model.bean.BasicCommon;
import base.model.connect.KafkaConnectOut;
import base.model.connect.bean.KafkaMsg;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:33
 * @description：kafka接收消息的控制actor
 */
public class DeviceCloudActor extends AbstractCloudControlActor {

    private static Logger logger = Logger.getLogger(DeviceCloudActor.class.getName());
//    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;

    private KafkaConnectOut kafkaConnectOut;

    private ActorRef<BasicCommon> kafkaConnectInActorRef;

    private DeviceModel deviceModel;

//    private List<String> subscribeTopics;

//    private List<EdgeDevice> edgeDevices;

    public static Behavior<BasicCommon> create(DeviceModel deviceModel, ActorRef<BasicCommon> kafkaConnectInActorRef) {
        return Behaviors.setup(context -> new DeviceCloudActor(context, deviceModel, kafkaConnectInActorRef));
    }

    public DeviceCloudActor(ActorContext<BasicCommon> context, DeviceModel deviceModel, ActorRef<BasicCommon> kafkaConnectInActorRef) {
        super(context);
        logger.log(Level.INFO, "DeviceCloudActor pre init...");
        this.ref = context.getSelf();
        this.deviceModel = deviceModel;
        this.kafkaConnectInActorRef = kafkaConnectInActorRef;
        upConnectOut();
        downConnectIn();
        createEdgeActorAction();
        logger.log(Level.INFO, "DeviceCloudActor init...");
    }

    public KafkaConnectOut getKafkaConnectOut() {
        return kafkaConnectOut;
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
//                .onMessage(CreateEdgeActorMsg.class, this::onCreateEdgeActorAction)
                .build();
    }

    private void createEdgeActorAction() {

        KafkaMsg kafkaMsg = new KafkaMsg();
        kafkaMsg.setTopic("edge.edge-pod-1");
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String key = df.format(new Date());
        kafkaMsg.setKey(TopicKey.CREATE_EDGE_ACTOR);
        String jsonString = JSON.toJSONString(deviceModel);
        kafkaMsg.setValue(jsonString);
//        System.out.println("999" + kafkaMsg);
        logger.log(Level.INFO, "DeviceCloudActor " + kafkaMsg);
        kafkaConnectOut.sendMessageForgetResult(kafkaMsg);
//        System.out.println(kafkaMsg);
    }


    private Behavior<BasicCommon> onKafkaMsgInAction(KafkaMsg kafkaMsg) {
        handleKafkaMsg(kafkaMsg);
        return this;
    }

    public void handleKafkaMsg(KafkaMsg kafkaMsg) {
        System.out.println("device cloud msg " + kafkaMsg);
    }

    @Override
    public void downConnectIn() {
        KafkaConfig kafkaConfig = deviceModel.getKafkaConfig();
        SubscribeTopic subscribeTopic = new SubscribeTopic();
        String topic = "cloud." + kafkaConfig.getTopic().replace("/", ".");
        subscribeTopic.setRef(ref);
        subscribeTopic.setTopic(topic);
        kafkaConnectInActorRef.tell(subscribeTopic);
    }

    @Override
    public void upConnectOut() {
        this.kafkaConnectOut = new KafkaConnectOut();
    }

//
//    public static void main(String[] args) {
//        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "cc3200");
//        KafkaConfig kafkaConfig = new KafkaConfig();
//        kafkaConfig.setServer("192.168.123.131:9092");
//        kafkaConfig.setGroupId("1");
//        List<String> list = new ArrayList<>();
////        list.add("cc3200-1");
//        list.add("cloud-control-1");
//        kafkaConfig.setTopics(list);
//        ActorRef<BasicCommon> ref = system.systemActorOf(CloudControlActor.create(kafkaConfig),
//                "cloud-control-1", Props.empty());
//
//
//
//
//        MqttConfig mqttConfig1 = new MqttConfig("cc3200/humidity", "tcp://192.168.123.247:1883", null, null, "123456");
////        mqttConfig1.setTopic( "cc3200/humidity");
////        mqttConfig1.setBrokerUrl("tcp://192.168.123.247:1883");
////        mqttConfig1.setClientId("123456");
//
//        KafkaConfig kafkaConfig1 = new KafkaConfig("192.168.123.131:9092", null, list);
////        kafkaConfig1.setServer("192.168.123.131:9092");
////        kafkaConfig1.setTopics(list);
//        DeviceModel model = new DeviceModel();
//        model.setKafkaConfig(kafkaConfig);
//        model.setMqttConfig(mqttConfig1);
//        model.setRealName("cc3200-1");
//        CreateEdgeActorMsg createEdgeActorMsg = new CreateEdgeActorMsg();
//        createEdgeActorMsg.setModel(model);
//
//        ref.tell(createEdgeActorMsg);
//    }
}
