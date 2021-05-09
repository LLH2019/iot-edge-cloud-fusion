package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.SubscribeTopic;
import base.type.TopicKey;
import cloud.global.GlobalActorRefName;
import cloud.global.GlobalAkkaPara;
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
    private final ActorRef<BasicCommon> ref;

    private KafkaConnectOut kafkaConnectOut;

    private final ActorRef<BasicCommon> kafkaConnectInActorRef;

    private final DeviceModel deviceModel;


    public static Behavior<BasicCommon> create(DeviceModel deviceModel) {
        return Behaviors.setup(context -> new DeviceCloudActor(context, deviceModel));
    }

    public DeviceCloudActor(ActorContext<BasicCommon> context, DeviceModel deviceModel) {
        super(context);
        logger.log(Level.INFO, "DeviceCloudActor pre init...");
        this.ref = context.getSelf();
        this.deviceModel = deviceModel;
        this.kafkaConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.CLOUD_KAFKA_CONNECT_IN_ACTOR);
        upConnectOut();
        downConnectIn();
        createEdgeActorAction();
        logger.log(Level.INFO, "DeviceCloudActor init...");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .build();
    }

    private void createEdgeActorAction() {
        KafkaMsg kafkaMsg = new KafkaMsg();
        kafkaMsg.setTopic("edge.edge-pod-1");
        kafkaMsg.setKey(TopicKey.CREATE_EDGE_ACTOR);
        String jsonString = JSON.toJSONString(deviceModel);
        kafkaMsg.setValue(jsonString);
        logger.log(Level.INFO, "DeviceCloudActor " + kafkaMsg);
        kafkaConnectOut.sendMessageForgetResult(kafkaMsg);
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
}
