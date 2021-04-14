package example.cc3200.kafka;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class CommonKafkaPublishDemo {

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        Properties kafkaPropertie = new Properties();
//        //配置broker地址，配置多个容错
//        kafkaPropertie.put("bootstrap.servers", "192.168.123.131:9092");
//        //配置key-value允许使用参数化类型
//        kafkaPropertie.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
//        kafkaPropertie.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
//
//        KafkaProducer kafkaProducer = new KafkaProducer(kafkaPropertie);
//
//        ProducerRecord<String, String> record = new ProducerRecord<String, String>("testTopic","key1","hello world");
//
//        kafkaProducer.send(record);
//
//    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties kafkaPropertie = new Properties();
        //配置broker地址，配置多个容错
        kafkaPropertie.put("bootstrap.servers", "192.168.123.131:9092");
        //配置key-value允许使用参数化类型
        kafkaPropertie.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        kafkaPropertie.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer kafkaProducer = new KafkaProducer(kafkaPropertie);
        //创建消息对象，第一个为参数topic,第二个参数为key,第三个参数为value
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("testTopic","key1","hello world");

        //同步发送方式,get方法返回结果
        RecordMetadata metadata = (RecordMetadata) kafkaProducer.send(record).get();
        System.out.println("broker返回消息发送信息" + metadata);

    }
}

