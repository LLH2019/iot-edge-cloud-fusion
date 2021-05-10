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
    private ActorRef<BasicCommon> edgeMqttConnectInActorRef;

    public EdgeMqttConnectIn() {
        this.edgeMqttConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.EDGE_MQTT_CONNECT_IN_ACTOR);
        init();
    }

    private void init() {
        try {
            logger.log(Level.INFO, "MqttConnectIn init... ");
            MqttClient client = new MqttClient(GlobalMqttConfig.brokenUrl, GlobalMqttConfig.clientId);
            MqttConnectOptions options = new MqttConnectOptions();
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
//            System.out.println("666");
            client.setCallback(new PushCallback(edgeMqttConnectInActorRef));
//            System.out.println("222");
            String str = "cc3200/1111/#";
//            MqttTopic topic = client.getTopic(str);
//            System.out.println(str);
//            MqttTopic topic = client.getTopic(m);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //遗嘱
//            options.setWill(topic, "close".getBytes(), 1, true);
            client.connect(options);
//            System.out.println("5555");
            //订阅消息
            int Qos = 1;//0：最多一次 、1：最少一次 、2：只有一次
//            String[] topic1 = {str};
            client.subscribe(str, Qos);

        } catch (MqttException e) {
            e.printStackTrace();
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
            // subscribe后得到的消息会执行到这里面
//            System.out.println("接收消息主题 : " + topic);
//            System.out.println("接收消息Qos : " + message.getQos());
//            System.out.println("接收消息内容 : " + new String(message.getPayload()));
//            System.out.println("1111111");

            MqttInMsg msg = new MqttInMsg();
            msg.setMsg(new String(message.getPayload()));
            msg.setTopic(topic);
            logger.log(Level.INFO, "MQTT msg : " + msg);
//            CC3200Actor.TemperatureUpload upload = new CC3200Actor.TemperatureUpload(new String(message.getPayload()));
            ref.tell(msg);
        }

    }

}
