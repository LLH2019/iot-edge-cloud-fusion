package things.model.connect;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.KafkaMsg;

import java.util.Properties;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：kafka向外发布消息
 */
public class KafkaConnectOut {
    private KafkaProducer kafkaProducer;

    public KafkaConnectOut(KafkaConfig kafkaConfig) {
        Properties kafkaPropertie = new Properties();
        //配置broker地址，配置多个容错
        kafkaPropertie.put("bootstrap.servers", kafkaConfig.getServer());
        //配置key-value允许使用参数化类型
        kafkaPropertie.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaPropertie.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer(kafkaPropertie);

    }

    public void sendMessageForgetResult(KafkaMsg kafkaMsg) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                kafkaMsg.getTopic(), kafkaMsg.getKey(), kafkaMsg.getValue()
        );
        kafkaProducer.send(record);
//        kafkaProducer.close();
    }

    public void sendMessageForgetResult(String topic, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                topic, key, value
        );
        kafkaProducer.send(record);
//        kafkaProducer.close();
    }

    public RecordMetadata sendMessageSync(String topic, String key, String value) throws Exception{
        ProducerRecord<String,String> record = new ProducerRecord<String,String>(
                topic, key,value
        );
        RecordMetadata result = (RecordMetadata) kafkaProducer.send(record).get();
        return result;
    }

    public void sendMessageCallback(String topic, String key, String value){
        ProducerRecord<String,String> record = new ProducerRecord<String,String>(
                topic, key,value
        );
        kafkaProducer.send(record,new MyProducerCallback());
    }



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
    
    

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        Properties kafkaPropertie = new Properties();
//        //配置broker地址，配置多个容错
//        kafkaPropertie.put("bootstrap.servers", "192.168.123.131:9092");
//        //配置key-value允许使用参数化类型
//        kafkaPropertie.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
//        kafkaPropertie.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
//
//        KafkaProducer kafkaProducer = new KafkaProducer(kafkaPropertie);
//        //创建消息对象，第一个为参数topic,第二个参数为key,第三个参数为value
//        ProducerRecord<String, String> record = new ProducerRecord<String, String>("testTopic","key1","hello world");
//
//        //同步发送方式,get方法返回结果
//        RecordMetadata metadata = (RecordMetadata) kafkaProducer.send(record).get();
//        System.out.println("broker返回消息发送信息" + metadata);
//
//    }

    private static class MyProducerCallback implements org.apache.kafka.clients.producer.Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        }
    }
}

