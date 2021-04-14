package example.cc3200.kafka;

import akka.actor.typed.ActorRef;
import example.cc3200.actor.CC3200ServerActor;
import example.cc3200.bean.KafkaData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.*;

public class CommonKafkaConsumerDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonKafkaConsumerDemo.class);

    private static ActorRef<CC3200ServerActor.Command> ref;

//    public CommonKafkaConsumerDemo(ActorRef<CC3200ServerActor.Command> ref) {
//        this.ref = ref;
//        init();
//    }


    public static void init(ActorRef<CC3200ServerActor.Command> ref) {
        ref = ref;
        Properties kafkaPropertie = new Properties();
        //配置broker地址，配置多个容错
        kafkaPropertie.put("bootstrap.servers", "192.168.123.131:9092");
        //配置key-value允许使用参数化类型，反序列化
        kafkaPropertie.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        kafkaPropertie.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        //指定消费者所属的群组
        kafkaPropertie.put("group.id","1");
        //创建KafkaConsumer，将kafkaPropertie传入。
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(kafkaPropertie);
        /*订阅主题，这里使用的是最简单的订阅testTopic主题，这里也可以出入正则表达式，来区分想要订阅的多个指定的主题，如：
         *Pattern pattern = new Pattern.compile("testTopic");
         * consumer.subscribe(pattern);
         */

        List<String> list = new ArrayList<>();
        list.add("cc3200-1");
        list.add("cc3200-2");
        consumer.subscribe(list);
        //轮询消息
        while (true) {
            //获取ConsumerRecords，一秒钟轮训一次
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            //消费消息，遍历records
            for (ConsumerRecord<String, String> r : records) {
                KafkaData data = new KafkaData();
                data.setTopic(r.topic());
                data.setKey(r.key());
                data.setValue(r.value());
                ref.tell(data);
//                LOGGER.error("partition:", r.partition());
//                LOGGER.error("topic:", r.topic());
//                LOGGER.error("offset:", r.offset());
                System.out.println(r.topic() + ":" + r.key() + ":" + r.value());
            }
//            Thread.sleep(1000);
        }
    }
}
