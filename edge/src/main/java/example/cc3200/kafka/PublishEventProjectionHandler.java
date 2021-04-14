package example.cc3200.kafka;

import akka.Done;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.kafka.javadsl.SendProducer;
import akka.projection.eventsourced.EventEnvelope;
import akka.projection.javadsl.Handler;
import example.cc3200.bean.PersistenceMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

public class PublishEventProjectionHandler extends Handler<EventEnvelope<PersistenceMessage.Event>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String topic;
    private final SendProducer<String, String> sendProducer;

    public PublishEventProjectionHandler(String topic, SendProducer<String, String> sendProducer) {
        this.topic = topic;
        this.sendProducer = sendProducer;
    }


    @Override
    public CompletionStage<Done> process(EventEnvelope<PersistenceMessage.Event> envelope)
            throws Exception, Exception {
        PersistenceMessage.Event event = envelope.event();
        String key = event.msgId;
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<String, String>(topic, key, event.msgId);

        return sendProducer
                .send(producerRecord)
                .thenApply(
                        recordMetadata ->{
                            logger.info(
                                    "Published event [{}] to topic/partition {}/{}",
                                    event,
                                    topic,
                                    recordMetadata.partition());
                            return Done.done();
                        });
    }

//    public static void main(String[] args) {
//
//        ProducerSettings<String, String> producerSettings = producerDefaults();
//        SendProducer<String, String> producer = new SendProducer<>(producerSettings, system);
//        PublishEventProjectionHandler handler = new PublishEventProjectionHandler("11")
//    }
}
