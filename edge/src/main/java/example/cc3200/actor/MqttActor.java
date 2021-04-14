package example.cc3200.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import example.cc3200.bean.Command;
import example.cc3200.bean.MqttConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MqttActor extends AbstractBehavior<Command> {


    public final MqttConfig mqttConfig;

    public final ActorRef<Command> ref;

    public MqttActor(ActorContext<Command> context, MqttConfig mqttConfig, ActorRef<Command> ref) {
        super(context);
        this.mqttConfig = mqttConfig;
        this.ref = ref;
        init(mqttConfig);

    }

    private void init(MqttConfig mqttConfig) {
        try {
            MqttClient client = new MqttClient(mqttConfig.brokerUrl, mqttConfig.clientId);
            System.out.println("777");
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
            System.out.println("666");
            client.setCallback(new PushCallback(ref));
            System.out.println("222");
            MqttTopic topic = client.getTopic(mqttConfig.topic);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //遗嘱
            options.setWill(topic, "close".getBytes(), 1, true);
            client.connect(options);
            //订阅消息
            int[] Qos = {1};//0：最多一次 、1：最少一次 、2：只有一次
            String[] topic1 = {mqttConfig.topic};
            client.subscribe(topic1, Qos);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public static Behavior<Command> create(MqttConfig mqttConfig, ActorRef<Command> ref) {
        System.out.println("444");
        return Behaviors.setup(context -> new MqttActor(context, mqttConfig, ref));
    }


    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder().build();
    }




}
