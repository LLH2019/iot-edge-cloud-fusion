package cloud.connect;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.RestartSettings;
import akka.stream.javadsl.RestartSource;
import base.model.bean.BasicCommon;
import base.model.connect.bean.KafkaMsg;
import cloud.front.GetKafkaMsg;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/5/8 22:38
 * @description：云端kafka接收消息
 */
public class CloudKafkaConsumer {
    private static Logger logger = Logger.getLogger(CloudKafkaConsumer.class.getName());

    private ActorRef<BasicCommon> kafkaConnectInActorRef;
    private Set<String> topics = new HashSet<>();

    public CloudKafkaConsumer(ActorSystem<?> system, ActorRef<BasicCommon> kafkaConnectInActorRef) {
        this.kafkaConnectInActorRef = kafkaConnectInActorRef;
        System.out.println("CloudKafkaConsumer init... " + kafkaConnectInActorRef);
        init(system);
    }

    public  void init(ActorSystem<?> system) {
        String topic =
                "cloud.cc3200.1111";

        String topic2 =
                "cloud.cc3200.3333";
        final Config config = system.settings().config().getConfig("akka.kafka.consumer");
        final ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(config, new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers("192.168.123.131:9092")
                        .withGroupId("group1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        ConsumerSettings<String, String> consumerSettings =
//                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
//                        .withGroupId("1");

        CommitterSettings committerSettings = CommitterSettings.create(system);

        Duration minBackoff = Duration.ofSeconds(1);
        Duration maxBackoff = Duration.ofSeconds(30);
        double randomFactor = 0.1;

        RestartSource
                .onFailuresWithBackoff(
                        RestartSettings.create(minBackoff, maxBackoff, randomFactor),
                        () -> {
                            return Consumer.committableSource(
                                    consumerSettings, Subscriptions.topics(topic, topic2))
                                    .mapAsync(
                                            1,
                                            msg -> handleRecord(msg.record()).thenApply(done -> msg.committableOffset()))
                                    .via(Committer.flow(committerSettings));
                        })
                .run(system);
    }

    private CompletionStage<Done> handleRecord(ConsumerRecord<String, String> record) {
//        System.out.println(record);
        KafkaMsg msg = new KafkaMsg();
        msg.setTopic(record.topic());
        msg.setKey(record.key());
        msg.setValue(record.value());
        System.out.println(kafkaConnectInActorRef + " " + msg);
        kafkaConnectInActorRef.tell(msg);

        GetKafkaMsg.kafkaMsg = msg;
//        TotalInfo.deviceNums =
//        if("close".equals(msg.getValue())) {
//            System.out.println("333333");
//        } else if(!topics.contains(msg.getTopic())) {
//            topics.add(msg.getTopic());
//            TotalInfo.deviceNums = topics.size();
//            TotalInfo.deviceSets.add(msg.getTopic());
//            DeviceInfo deviceInfo = new DeviceInfo();
//            deviceInfo.setName(msg.getTopic());
//            Map<String, String> valueMap = new HashMap<>();
//            String[] strs = msg.getValue().split(":");
//            valueMap.put(strs[0], strs[1]);
//            deviceInfo.setPropertyMap(valueMap);
//            TotalInfo.deviceInfoMap.put(msg.getTopic(), deviceInfo);
//            System.out.println("111111111");
//        } else {
//            DeviceInfo deviceInfo = TotalInfo.deviceInfoMap.get(msg.getTopic());
//            Map<String, String> valueMap = deviceInfo.getPropertyMap();
//            String[] strs = msg.getValue().split(":");
//            if(strs.length == 2) {
//                valueMap.put(strs[0], strs[1]);
//                deviceInfo.setPropertyMap(valueMap);
//            }
//            System.out.println("222222");
//        }
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
