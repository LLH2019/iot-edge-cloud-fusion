package edge.connect;

import base.model.connect.bean.KafkaMsg;
import edge.global.GlobalKafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：kafka向外发布消息
 */
public class KafkaConnectOut {
    private KafkaProducer kafkaProducer;

    public KafkaConnectOut() {
        Properties kafkaProperties = new Properties();
        //配置broker地址，配置多个容错
        kafkaProperties.put("bootstrap.servers", GlobalKafkaConfig.server);
        //配置key-value允许使用参数化类型
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer(kafkaProperties);

    }

    public void sendMessageForgetResult(KafkaMsg kafkaMsg) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                kafkaMsg.getTopic(), kafkaMsg.getKey(), kafkaMsg.getValue()
        );
        kafkaProducer.send(record);
    }

    public void sendMessageForgetResult(String topic, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                topic, key, value
        );
        kafkaProducer.send(record);
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

    private static class MyProducerCallback implements org.apache.kafka.clients.producer.Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        }
    }
}

