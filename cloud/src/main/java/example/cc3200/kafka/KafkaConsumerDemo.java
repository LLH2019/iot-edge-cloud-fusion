
package example.cc3200.kafka;

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.RestartSettings;
import akka.stream.javadsl.RestartSource;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class KafkaConsumerDemo {
  private static final Logger log = LoggerFactory.getLogger(KafkaConsumerDemo.class);

  public static void init(ActorSystem<?> system) {
    String topic =
        system
            .settings()
            .config()
            .getString("cc3200-service.kafka.topic");
    ConsumerSettings<String,String> consumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withGroupId("cc3200");
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

  private static CompletionStage<Done> handleRecord(ConsumerRecord<String, String> record) {
    String str = record.value();
    System.out.println(str);

    return CompletableFuture.completedFuture(Done.getInstance());
  }
}

