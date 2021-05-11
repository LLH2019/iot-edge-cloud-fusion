package edge.connect;

import akka.actor.typed.ActorRef;
import edge.global.GlobalKafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import base.model.bean.BasicCommon;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.KafkaMsg;

import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:42
 * @description：边缘端 kafka 接入数据
 */
public class EdgeKafkaConnectIn {
    private static Logger logger = Logger.getLogger(EdgeKafkaConnectIn.class.getName());
    private final ActorRef<BasicCommon> ref;
    private KafkaConsumer<String, String> consumer;

    public EdgeKafkaConnectIn(ActorRef<BasicCommon> ref) {
        this.ref = ref;
        init();
    }


    private void init() {
        Properties kafkaPropertie = new Properties();
        //配置broker地址，配置多个容错
        kafkaPropertie.put("bootstrap.servers", GlobalKafkaConfig.server);
        //配置key-value允许使用参数化类型，反序列化
        kafkaPropertie.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        kafkaPropertie.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        //指定消费者所属的群组
        kafkaPropertie.put("group.id",GlobalKafkaConfig.groupId);
        //创建KafkaConsumer，将kafkaPropertie传入。
        consumer = new KafkaConsumer<String, String>(kafkaPropertie);

        Pattern pattern = Pattern.compile(GlobalKafkaConfig.edge_in_topic);

        consumer.subscribe(pattern);
        logger.log(Level.INFO, "EdgeKafkaConnectIn is listening..." + GlobalKafkaConfig.edge_in_topic);
        //轮询消息
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(2000));
            //消费消息，遍历records
            for (ConsumerRecord<String, String> r : records) {
                KafkaMsg data = new KafkaMsg();
                data.setTopic(r.topic());
                data.setKey(r.key());
                data.setValue(r.value());
                logger.log(Level.INFO, ref + r.topic() + ":" + r.key() + ":" + r.value());
                ref.tell(data);
                System.out.println("kafkaConnectIn " + r.topic() + ":" + r.key() + ":" + r.value());
            }
        }
    }
}
