package example.cc3200.kafka;

import akka.actor.typed.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.SendProducer;
import com.typesafe.config.Config;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.orm.jpa.JpaTransactionManager;

public class KafkaConnector {


    public static void init(ActorSystem<?> system) {
        final Config config = system.settings().config().getConfig("akka.kafka.producer");
        final ProducerSettings<String, String> producerSettings =
                ProducerSettings.create(config, new StringSerializer(), new StringSerializer())
                        .withBootstrapServers("localhost:9092");
        final org.apache.kafka.clients.producer.Producer<String, String> kafkaProducer =
                producerSettings.createKafkaProducer();

    }

}
