package edge.connect;

import akka.actor.typed.ActorRef;
import base.model.bean.BasicCommon;
import base.model.connect.bean.MqttConfig;
import base.model.connect.bean.MqttInMsg;
import edge.global.GlobalActorRefName;
import edge.global.GlobalAkkaPara;
import edge.global.GlobalMqttConfig;
import org.eclipse.paho.client.mqttv3.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：MQTT接收外部消息
 */
public class EdgeMqttConnectIn {
    private static Logger logger = Logger.getLogger(EdgeMqttConnectIn.class.getName());
    private final ActorRef<BasicCommon> edgeMqttConnectInActorRef;
    private static MqttClient mqttClient = null;
    private static MqttConnectOptions options = null;

    public EdgeMqttConnectIn() {
        this.edgeMqttConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.EDGE_MQTT_CONNECT_IN_ACTOR);
        init();
    }

    private void init() {
        try {
            logger.log(Level.INFO, "MqttConnectIn init... ");
            this.mqttClient = new MqttClient(GlobalMqttConfig.brokenUrl, GlobalMqttConfig.clientId);
            this.options = new MqttConnectOptions();
            options.setCleanSession(false);
            // 设置连接的用户名
//            options.setUserName(mqttConfig.userName);
            // 设置连接的密码
//            options.setPassword(mqttConfig.password.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置断开后重新连接
            options.setAutomaticReconnect(true);
            // 设置回调
            mqttClient.setCallback(new PushCallback(edgeMqttConnectInActorRef));
            mqttClient.connect(options);
            //订阅消息
            int Qos = 1;//0：最多一次 、1：最少一次 、2：只有一次
            mqttClient.subscribe(GlobalMqttConfig.topic, Qos);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String pubTopic, String message) {
        if (null != mqttClient && mqttClient.isConnected()) {
            MqttMessage mqttMessage = new MqttMessage();
//            mqttMessage.setQos(qos);
            mqttMessage.setPayload(message.getBytes());
            MqttTopic topic = mqttClient.getTopic(pubTopic);
            if (null != topic) {
                try {
                    MqttDeliveryToken publish = topic.publish(mqttMessage);
                    if (!publish.isComplete()) {
                        logger.log(Level.INFO, "消息发布成功:: "+ pubTopic + message);
                    }
                } catch (MqttException e) {
                    logger.log(Level.INFO,"消息发布失败:: " + e.getMessage());
                }
            }

        } else {
            reConnect();
            publishMessage(pubTopic, message);
        }

    }

    //重新连接
    public void reConnect() {
        if (null != mqttClient) {
            if (!mqttClient.isConnected()) {
                if (null != options) {
                    try {
                        mqttClient.connect(options);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            init();
        }

    }

    public class PushCallback implements MqttCallback {

        public ActorRef<BasicCommon> ref;

        public PushCallback(ActorRef<BasicCommon> ref) {
            this.ref = ref;
        }

        public void connectionLost(Throwable cause) {

            // 连接丢失后，一般在这里面进行重连
            System.out.println("连接断开，可以做重连");
        }

        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete---------" + token.isComplete());
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            MqttInMsg msg = new MqttInMsg();
            msg.setMsg(new String(message.getPayload()));
            msg.setTopic(topic);
            logger.log(Level.INFO, "MQTT msg : " + msg);
            ref.tell(msg);
        }

    }

}
