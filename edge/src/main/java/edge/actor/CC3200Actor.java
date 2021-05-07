//package edge.actor;
//
//import akka.actor.typed.ActorSystem;
//import akka.actor.typed.Behavior;
//import akka.actor.typed.Props;
//import akka.actor.typed.javadsl.ActorContext;
//import akka.actor.typed.javadsl.Behaviors;
//import base.model.bean.BasicCommon;
//import base.model.connect.bean.KafkaConfig;
//import base.model.connect.bean.MqttConfig;
//import base.model.connect.bean.MqttInMsg;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @author ：LLH
// * @date ：Created in 2021/4/16 11:00
// * @description：CC3200 actor类，作为测试之用
// */
//public class CC3200Actor extends DeviceActor {
//
//    public CC3200Actor(ActorContext<BasicCommon> context, MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
//        super(context, mqttConfig, kafkaConfig);
//        System.out.println("CC3200Actor");
//    }
//
//    @Override
//    public void handleMqttMsg(MqttInMsg msg) {
//        System.out.println("handleMqttMsg: " + msg.getMsg());
//        sendToKafka(msg);
////        System.out.println(msg.getMsg());
////        super.handleMqttMsg(msg);
//    }
//
//    private void sendToKafka(MqttInMsg msg) {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String key = df.format(new Date()) + "-temperature";
//        getKafkaConnectOut().sendMessageForgetResult("cc3200-3", key, msg.getMsg());
//    }
//
//    public static Behavior<BasicCommon> create(MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
//        System.out.println("create");
//        return Behaviors.setup(context -> new CC3200Actor(context, mqttConfig, kafkaConfig));
//    }
//
//
//    public static void main(String[] args) {
//        ActorSystem<Void> system =ActorSystem.create(Behaviors.empty(), "cc3200");
//        System.out.println("111");
//        init(system);
//    }
//
//    private static void init(ActorSystem<Void> system) {
//        MqttConfig mqttConfig1 = new MqttConfig();
//        mqttConfig1.setTopic( "cc3200/humidity");
//        mqttConfig1.setBrokerUrl("tcp://192.168.123.247:1883");
//        mqttConfig1.setClientId("123456");
//
//        KafkaConfig kafkaConfig1 = new KafkaConfig();
//        kafkaConfig1.setServer("192.168.123.131:9092");
//
//        system.systemActorOf(CC3200Actor.create(mqttConfig1, kafkaConfig1), "cc3200", Props.empty());
////        CC3200Actor.create(mqttConfig1, kafkaConfig1);
//    }
//}
