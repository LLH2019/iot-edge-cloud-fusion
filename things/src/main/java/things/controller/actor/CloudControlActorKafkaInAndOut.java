package things.controller.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import things.brain.bean.CreateEdgeActorMsg;
import things.brain.bean.EdgeDevice;
import things.model.bean.AbstractModel;
import things.model.bean.BasicCommon;
import things.model.connect.KafkaConnectIn;
import things.model.connect.KafkaConnectOut;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    private AbstractModel model;


    private List<EdgeDevice> edgeDevices;

    public static Behavior<BasicCommon> create(KafkaConfig kafkaConfig) {
        return Behaviors.setup(context -> new CloudControlActorKafkaInAndOut(context, kafkaConfig));
    }

    public CloudControlActorKafkaInAndOut(ActorContext<BasicCommon> context, KafkaConfig kafkaConfig) {
        super(context);
        this.ref = context.getSelf();
        this.kafkaConfig = kafkaConfig;
        downConnectIn();
        upConnectOut();
//        init();
    }

    public KafkaConnectOut getKafkaConnectOut() {
        return kafkaConnectOut;
    }

    public void createEdgeActor(KafkaMsg kafkaMsg) {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String key = df.format(new Date()) + "-temperature";
        getKafkaConnectOut().sendMessageForgetResult(kafkaMsg.getTopic(), kafkaMsg.getKey(), kafkaMsg.getValue());
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(AbstractModel.class, this::onSetAbstractModelAction)
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .onMessage(CreateEdgeActorMsg.class, this::onCreateEdgeActorAction)
                .build();
    }

    private Behavior<BasicCommon> onCreateEdgeActorAction(CreateEdgeActorMsg a) {
        KafkaMsg kafkaMsg = new KafkaMsg();
        kafkaMsg.setTopic("edge-pod-1");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = df.format(new Date()) + "-temperature";
        kafkaMsg.setKey(key);



        return this;
    }

    private Behavior<BasicCommon> onSetAbstractModelAction(AbstractModel model) {
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
        this.kafkaConnectOut = new KafkaConnectOut(kafkaConfig);
    }


}
