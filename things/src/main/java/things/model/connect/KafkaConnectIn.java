package things.model.connect;

import akka.actor.typed.ActorRef;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import things.model.bean.BasicCommon;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;

import java.time.Duration;
import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:42
 * @description：kafka 接入数据
 */
public class KafkaConnectIn {

    final Set<String> subscribedTopics = new HashSet<>();
    private ActorRef<BasicCommon> ref;
    private KafkaConfig kafkaConfig;
    KafkaConsumer<String, String> consumer;

    public KafkaConnectIn(KafkaConfig kafkaConfig, ActorRef<BasicCommon> ref) {
        this.kafkaConfig = kafkaConfig;
        this.ref = ref;
        init();
    }

    public void addTopics(List<String> topics) {
        System.out.println("addTopics");
        for(String topic : topics) {
            subscribedTopics.add(topic);
        }
        consumer.subscribe(new ArrayList<>(subscribedTopics));
        System.out.println("addTopics--");
    }

    private void init() {
        ref = ref;
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

        consumer.subscribe(kafkaConfig.getTopics());
        //轮询消息
        while (true) {
            //获取ConsumerRecords，一秒钟轮训一次
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            //消费消息，遍历records
            for (ConsumerRecord<String, String> r : records) {
                KafkaMsg data = new KafkaMsg();
                data.setTopic(r.topic());
                data.setKey(r.key());
                data.setValue(r.value());
                ref.tell(data);
//                LOGGER.error("partition:", r.partition());
//                LOGGER.error("topic:", r.topic());
//                LOGGER.error("offset:", r.offset());
                System.out.println("kafkaConnectIn " + r.topic() + ":" + r.key() + ":" + r.value());
            }
//            Thread.sleep(1000);
        }
    }
}
