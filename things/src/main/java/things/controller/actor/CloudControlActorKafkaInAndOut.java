package things.controller.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.alibaba.fastjson.JSON;
import things.base.TopicKey;
import things.brain.bean.CreateEdgeActorMsg;
import things.brain.bean.EdgeDevice;
import things.model.bean.DeviceModel;
import things.model.bean.BasicCommon;
import things.model.connect.KafkaConnectIn;
import things.model.connect.KafkaConnectOut;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;
import things.model.connect.bean.MqttConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:33
 * @description：kafka接收消息的控制actor
 */
public class CloudControlActorKafkaInAndOut extends CloudControlActor {

    private KafkaConfig kafkaConfig;
    private ActorRef<BasicCommon> ref;

    private KafkaConnectOut kafkaConnectOut;

    private DeviceModel model;


    private List<EdgeDevice> edgeDevices;

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new CloudControlActorKafkaInAndOut(context, kafkaConfig));
    }

    public CloudControlActorKafkaInAndOut(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.ref = context.getSelf();
        this.kafkaConfig = kafkaConfig;


        new Thread(){
            @Override
            public void run() {
                downConnectIn();
            }
        }.start();
        upConnectOut();
//        init();
    }

    public KafkaConnectOut getKafkaConnectOut() {
        return kafkaConnectOut;
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(DeviceModel.class, this::onSetAbstractModelAction)
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .onMessage(CreateEdgeActorMsg.class, this::onCreateEdgeActorAction)
                .build();
    }

    private Behavior<BasicCommon> onCreateEdgeActorAction(CreateEdgeActorMsg a) {

        KafkaMsg kafkaMsg = new KafkaMsg();
        kafkaMsg.setTopic("edge-pod-1");
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String key = df.format(new Date());
        kafkaMsg.setKey(TopicKey.CREATE_EDGE_ACTOR);
        String jsonString = JSON.toJSONString(a);
        kafkaMsg.setValue(jsonString);
        kafkaConnectOut.sendMessageForgetResult(kafkaMsg);
        System.out.println(kafkaMsg);
        return this;
    }

    private Behavior<BasicCommon> onSetAbstractModelAction(DeviceModel model) {
        this.model = model;
        return this;
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
//        System.out.println("5555");
        this.kafkaConnectOut = new KafkaConnectOut(kafkaConfig);
    }


    public static void main(String[] args) {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "cc3200");
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServer("192.168.123.131:9092");
        kafkaConfig.setGroupId("1");
        List<String> list = new ArrayList<>();
//        list.add("cc3200-1");
        list.add("cloud-control-1");
        kafkaConfig.setTopics(list);
        ActorRef<BasicCommon> ref = system.systemActorOf(CloudControlActorKafkaInAndOut.create(kafkaConfig),
                "cloud-control-1", Props.empty());




        MqttConfig mqttConfig1 = new MqttConfig("cc3200/humidity", "tcp://192.168.123.247:1883", null, null, "123456");
//        mqttConfig1.setTopic( "cc3200/humidity");
//        mqttConfig1.setBrokerUrl("tcp://192.168.123.247:1883");
//        mqttConfig1.setClientId("123456");

        KafkaConfig kafkaConfig1 = new KafkaConfig("192.168.123.131:9092", null, list);
//        kafkaConfig1.setServer("192.168.123.131:9092");
//        kafkaConfig1.setTopics(list);
        DeviceModel model = new DeviceModel();
        model.setKafkaConfig(kafkaConfig);
        model.setMqttConfig(mqttConfig1);
        model.setRealName("cc3200-1");
        CreateEdgeActorMsg createEdgeActorMsg = new CreateEdgeActorMsg();
        createEdgeActorMsg.setModel(model);

        ref.tell(createEdgeActorMsg);
    }
}
