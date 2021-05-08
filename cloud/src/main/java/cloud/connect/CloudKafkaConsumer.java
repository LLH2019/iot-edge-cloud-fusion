package cloud.connect;

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.protobufv3.internal.Any;
import akka.protobufv3.internal.CodedInputStream;
import akka.protobufv3.internal.InvalidProtocolBufferException;
import akka.stream.RestartSettings;
import akka.stream.javadsl.RestartSource;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author ：LLH
 * @date ：Created in 2021/5/8 22:38
 * @description：云端kafka接收消息
 */
public class CloudKafkaConsumer {
    public static void init(ActorSystem<?> system) {
        String topic =
                "cloud.cc3200.1111";
        ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                        .withGroupId("1");

        CommitterSettings committerSettings = CommitterSettings.create(system);

        Duration minBackoff = Duration.ofSeconds(1);
        Duration maxBackoff = Duration.ofSeconds(30);
        double randomFactor = 0.1;

        RestartSource
                .onFailuresWithBackoff(
                        RestartSettings.create(minBackoff, maxBackoff, randomFactor),
                        () -> {
                            return Consumer.committableSource(
                                    consumerSettings, Subscriptions.topics(topic))
                                    .mapAsync(
                                            1,
                                            msg -> handleRecord(msg.record()).thenApply(done -> msg.committableOffset()))
                                    .via(Committer.flow(committerSettings));
                        })
                .run(system);
    }

    private static CompletionStage<Done> handleRecord(ConsumerRecord<String, String> record)
            throws InvalidProtocolBufferException {
        System.out.println(record);
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
