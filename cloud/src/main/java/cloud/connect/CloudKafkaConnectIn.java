package cloud.connect;

import akka.actor.typed.ActorRef;
import cloud.bean.KafkaMsgList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import base.model.bean.BasicCommon;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.KafkaMsg;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:42
 * @description：kafka 接入数据
 */
public class CloudKafkaConnectIn {

//    private Set<String> subscribedTopics = new HashSet<>();
//    private int topicNum;
    private static Logger logger = Logger.getLogger(CloudKafkaConnectIn.class.getName());

    private ActorRef<BasicCommon> ref;
    private KafkaConfig kafkaConfig;
    KafkaConsumer<String, String> consumer;

    public CloudKafkaConnectIn(KafkaConfig kafkaConfig, ActorRef<BasicCommon> ref) {
        this.kafkaConfig = kafkaConfig;
        this.ref = ref;
//        subscribedTopics.add(kafkaConfig.getTopic());
//        topicNum = subscribedTopics.size();
//        System.out.println("44444");
        init();
    }

//    public void addTopics(List<String> topics) {
//        System.out.println("addTopics");
//        for(String topic : topics) {
//            subscribedTopics.add(topic);
//        }
//        for (String topic: subscribedTopics) {
//            System.out.print(topic + " ");
//        }
//        System.out.println();
////        consumer.subscribe(new ArrayList<>(subscribedTopics));
//
//        System.out.println("addTopics--");
//    }

    private void init() {
        System.out.println("6666" + kafkaConfig + ref);
        Properties kafkaPropertie = new Properties();
        //配置broker地址，配置多个容错
        kafkaPropertie.put("bootstrap.servers", kafkaConfig.getServer());
        //配置key-value允许使用参数化类型，反序列化
        kafkaPropertie.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        kafkaPropertie.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        //指定消费者所属的群组
        kafkaPropertie.put("group.id",kafkaConfig.getGroupId());
        //创建KafkaConsumer，将kafkaPropertie传入。
        consumer = new KafkaConsumer<String, String>(kafkaPropertie);
        /*订阅主题，这里使用的是最简单的订阅testTopic主题，这里也可以出入正则表达式，来区分想要订阅的多个指定的主题，如：
         *Pattern pattern = new Pattern.compile("testTopic");
         * consumer.subscribe(pattern);
         */

//        System.out.println("222" + kafkaConfig.getTopic());
        String topic = "cloud.*";
        Pattern pattern = Pattern.compile(topic);
//        List<String> topics = new ArrayList<>();
//        topics.add(kafkaConfig.getTopic());
        consumer.subscribe(pattern);
        logger.log(Level.INFO, "EdgeKafkaConnectIn is listening..." + topic);
        //轮询消息
        while (true) {
            //获取ConsumerRecords，一秒钟轮训一次
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            //消费消息，遍历records
//            KafkaMsg outData = new KafkaMsg();
            List<KafkaMsg> msgs = new ArrayList<>();
            for (ConsumerRecord<String, String> r : records) {

                KafkaMsg data = new KafkaMsg();
                data.setTopic(r.topic());
                data.setKey(r.key());
                data.setValue(r.value());
                msgs.add(data);
//                logger.log(Level.INFO, "kafkaConnectIn " + r.topic() + ":" + r.key() + ":" + r.value());
//                System.out.println(ref  + " 222 " + data);

//                ref.tell(data);
//                synchronized (this) {

//                }
//                LOGGER.error("partition:", r.partition());
//                LOGGER.error("topic:", r.topic());
//                LOGGER.error("offset:", r.offset());
                System.out.println("kafkaConnectIn " + r.topic() + ":" + r.key() + ":" + r.value());
            }

            KafkaMsgList kafkaMsgList = new KafkaMsgList();
            kafkaMsgList.setKafkaMsgs(msgs);
            ref.tell(kafkaMsgList);


//            System.out.println("3333 " + topicNum);
//            if (subscribedTopics.size() != topicNum) {
//                topicNum = subscribedTopics.size();
//                System.out.println("3333 " + topicNum);
//                consumer.subscribe(subscribedTopics); // 重新订阅topic
//            }
//            Thread.sleep(1000);
        }
    }
}
