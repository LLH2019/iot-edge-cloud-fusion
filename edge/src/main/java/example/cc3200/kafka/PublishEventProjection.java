package example.cc3200.kafka;

import akka.actor.CoordinatedShutdown;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.SendProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PublishEventProjection {


    public static void init(ActorSystem<?> system) {
//        SendProducer<String, String> sendProducer = createProducer(system);
//        String topic = system.settings().config().getString("cc3200-service.kafka.topic");

    }

    private  SendProducer<String,String> createProducer(ActorSystem<?> system) {
        ProducerSettings<String, String>producerSettings =
                ProducerSettings.create(system, new StringSerializer(), new StringSerializer());
        SendProducer<String, String>sendProducer = new SendProducer<>(producerSettings, system);

        CoordinatedShutdown.get(system)
                .addTask(CoordinatedShutdown.PhaseActorSystemTerminate(),
                        "close-sendProducer",
                        () -> sendProducer.close());
        return sendProducer;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        PublishEventProjection projection = new PublishEventProjection();
        ActorSystem<Object> system =ActorSystem.create(Behaviors.empty(), "kafka");
        String topic = system.settings().config().getString("cc3200-service.kafka.topic");
        SendProducer<String, String> producer = projection.createProducer(system);
        try {
            CompletionStage<RecordMetadata> result =
                    producer.send(new ProducerRecord<>(topic, "key", "value"));
            // Blocking here for illustration only, you need to handle the future result
            RecordMetadata recordMetadata = result.toCompletableFuture().get(2, TimeUnit.SECONDS);
        } finally {
            producer.close().toCompletableFuture().get(1, TimeUnit.MINUTES);
        }

    }

}
