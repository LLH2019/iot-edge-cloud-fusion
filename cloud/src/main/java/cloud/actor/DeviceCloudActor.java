package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.Event;
import base.model.bean.Property;
import base.model.connect.bean.SubscribeTopic;
import base.type.TopicKey;
import cloud.front.DeviceInfo;
import cloud.front.TotalInfo;
import cloud.global.GlobalActorRefName;
import cloud.global.GlobalAkkaPara;
import com.alibaba.fastjson.JSON;
import base.model.bean.DeviceModel;
import base.model.bean.BasicCommon;
import cloud.connect.CloudKafkaConnectOut;
import base.model.connect.bean.KafkaMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:33
 * @description：kafka接收消息的控制actor
 */
public class DeviceCloudActor extends AbstractCloudControlActor {

    private static Logger logger = Logger.getLogger(DeviceCloudActor.class.getName());

    private final ActorRef<BasicCommon> ref;

    private CloudKafkaConnectOut cloudKafkaConnectOut;

    private final ActorRef<BasicCommon> kafkaConnectInActorRef;

    private final DeviceModel deviceModel;

    private final String realName;

    private final Map<String, String> propertyMap = new HashMap<>();

    private final List<String> eventList = new ArrayList<>();

    private static int index = 0;

    public static Behavior<BasicCommon> create(DeviceModel deviceModel) {
        return Behaviors.setup(context -> new DeviceCloudActor(context, deviceModel));
    }

    public DeviceCloudActor(ActorContext<BasicCommon> context, DeviceModel deviceModel) {
        super(context);
        logger.log(Level.INFO, "DeviceCloudActor pre init...");
        this.ref = context.getSelf();
        this.deviceModel = deviceModel;
        this.realName = "cloud." + deviceModel.getModel().getName() + "." + deviceModel.getModel().getNo();
        this.kafkaConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.CLOUD_KAFKA_CONNECT_IN_ACTOR);
        List<Property> properties = deviceModel.getModel().getProperties();
        if (properties != null) {
            for (Property p : properties) {
                propertyMap.put(p.getName(), "0");
            }
        }

        List<Event> events = deviceModel.getModel().getEvents();
        if (events != null) {
            for (Event e : events) {
                eventList.add(e.getName());
            }
        }

        initDeviceInfo();
        upConnectOut();
        downConnectIn();
        createEdgeActorAction();
        logger.log(Level.INFO, "DeviceCloudActor init...");
    }

    private void initDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setName(deviceModel.getModel().getName() + "-" + deviceModel.getModel().getNo());
        deviceInfo.setPropertyMap(propertyMap);
        deviceInfo.setEventList(eventList);
        TotalInfo.deviceNums++;
        TotalInfo.deviceSets.add(realName);
        TotalInfo.deviceInfoMap.put(realName, deviceInfo);
        logger.log(Level.INFO, "DeviceCloudActor initDeviceInfo...");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(KafkaMsg.class, this::onKafkaMsgInAction)
                .build();
    }



    private void createEdgeActorAction() {
        KafkaMsg kafkaMsg = new KafkaMsg();
        kafkaMsg.setTopic("edge." + GlobalAkkaPara.podList.get(index));
        index = (index+1)%(index+1);
        logger.log(Level.INFO, "Latest index num is : " + index);
        kafkaMsg.setKey(TopicKey.CREATE_EDGE_ACTOR);
        String jsonString = JSON.toJSONString(deviceModel);
        kafkaMsg.setValue(jsonString);
        logger.log(Level.INFO, "DeviceCloudActor " + kafkaMsg);
        cloudKafkaConnectOut.sendMessageForgetResult(kafkaMsg);
    }


    private Behavior<BasicCommon> onKafkaMsgInAction(KafkaMsg kafkaMsg) {
        System.out.println("device cloud actor ....");
        handleKafkaMsg(kafkaMsg);
        return this;
    }

    public void handleKafkaMsg(KafkaMsg msg) {
//        if("close".equals(msg.getValue())) {
//            logger.log(Level.INFO, "DeviceCloudActor : kafka msg content is close...");
//        } else {
//            String[] strs = msg.getValue().split(":");
//            if(strs.length == 2) {
//                propertyMap.put(strs[0], strs[1]);
//            }
//            System.out.println("DeviceCloudActor : " + strs);
//        }
        System.out.println("device cloud msg " + msg);
    }

    @Override
    public void downConnectIn() {
        SubscribeTopic subscribeTopic = new SubscribeTopic();
        subscribeTopic.setRef(ref);
        subscribeTopic.setTopic(realName);
        kafkaConnectInActorRef.tell(subscribeTopic);
    }

    @Override
    public void upConnectOut() {
        this.cloudKafkaConnectOut = new CloudKafkaConnectOut();
    }
}
