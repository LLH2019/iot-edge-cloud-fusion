package brain.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.type.TopicKey;
import com.alibaba.fastjson.JSON;
import base.model.bean.DeviceModel;
import base.model.bean.BasicCommon;
import base.model.connect.KafkaConnectOut;
import base.model.connect.bean.KafkaMsg;

import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:33
 * @description：kafka接收消息的控制actor
 */
public class CloudControlActor extends AbstractCloudControlActor {

//    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;

    private KafkaConnectOut kafkaConnectOut;

    private DeviceModel deviceModel;

    private List<String> subscribeTopics;

//    private List<EdgeDevice> edgeDevices;

    public static Behavior<BasicCommon> create(DeviceModel deviceModel) {
        return Behaviors.setup(context -> new CloudControlActor(context, deviceModel));
    }

    public CloudControlActor(ActorContext<BasicCommon> context, DeviceModel deviceModel) {
        super(context);
        this.ref = context.getSelf();
        this.deviceModel = deviceModel;
        upConnectOut();
        createEdgeActorAction();
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
        kafkaMsg.setTopic("llh.edge-pod-1");
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String key = df.format(new Date());
        kafkaMsg.setKey(TopicKey.CREATE_EDGE_ACTOR);
        String jsonString = JSON.toJSONString(deviceModel);
        kafkaMsg.setValue(jsonString);
        System.out.println("999" + kafkaMsg);
        kafkaConnectOut.sendMessageForgetResult(kafkaMsg);
        System.out.println(kafkaMsg);
    }


    private Behavior<BasicCommon> onKafkaMsgInAction(KafkaMsg kafkaMsg) {
        handleKafkaMsg(kafkaMsg);
        return this;
    }

    public void handleKafkaMsg(KafkaMsg kafkaMsg) {

    }

    @Override
    public void downConnectIn() {
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
