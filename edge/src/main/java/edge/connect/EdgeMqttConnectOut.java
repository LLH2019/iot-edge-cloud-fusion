package edge.connect;

import edge.global.GlobalMqttConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author wizzer@qq.com
 */
public class EdgeMqttConnectOut {
    private Logger logger = Logger.getLogger(EdgeMqttConnectOut.class.getName());

    public static MqttClient mqttClient = null;
    private static MemoryPersistence memoryPersistence = null;
    private static MqttConnectOptions mqttConnectOptions = null;

    private static String mqtt_host;
    private static String mqtt_clientId;
    private static int qos = 1;

    public void init() {
        mqtt_host = GlobalMqttConfig.brokenUrl;
        mqtt_clientId = GlobalMqttConfig.clientId;
        //初始化连接设置对象
        mqttConnectOptions = new MqttConnectOptions();
        //初始化MqttClient

        //true可以安全地使用内存持久性作为客户端断开连接时清除的所有状态
        mqttConnectOptions.setCleanSession(true);
        //设置连接超时
        mqttConnectOptions.setConnectionTimeout(30);
        //设置持久化方式
        memoryPersistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(mqtt_host, mqtt_clientId, memoryPersistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //设置连接和回调
        if (null != mqttClient) {
            if (!mqttClient.isConnected()) {
                //客户端添加回调函数
                mqttClient.setCallback(new MqttSenderCallback());
                //创建连接
                try {
                    logger.log(Level.INFO, "创建连接");
                    mqttClient.connect(mqttConnectOptions);
                } catch (MqttException e) {
                    logger.log(Level.WARNING, "创建连接失败 %s" +  e.getMessage());
                }

            }
        } else {
            logger.log(Level.WARNING, "mqttClient 为空");
        }
    }

    public void closeConnect() {
        //关闭存储方式
        if (null != memoryPersistence) {
            try {
                memoryPersistence.close();
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            }
        }

        //关闭连接
        if (null != mqttClient) {
            if (mqttClient.isConnected()) {
                try {
                    mqttClient.disconnect();
                    mqttClient.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
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
                if (null != mqttConnectOptions) {
                    try {
                        mqttClient.connect(mqttConnectOptions);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            init();
        }

    }

    public class MqttSenderCallback implements MqttCallback {

        public void connectionLost(Throwable cause) {

        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println("Client 接收消息主题 : " + topic);
            System.out.println("Client 接收消息Qos : " + message.getQos());
            System.out.println("Client 接收消息内容 : " + new String(message.getPayload()));
        }

        public void deliveryComplete(IMqttDeliveryToken token) {

        }

    }
}
