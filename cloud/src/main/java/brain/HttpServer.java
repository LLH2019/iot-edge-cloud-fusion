package brain;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import akka.http.javadsl.marshallers.jackson.Jackson;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import brain.bean.GetDeviceModelDoc;
import brain.bean.InsertMongoDBDoc;
import base.model.bean.*;
import base.model.connect.bean.KafkaConfig;
import base.model.connect.bean.MqttConfig;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:22
 * @description：http server
 */
public class HttpServer extends AllDirectives {
    private ActorRef<BasicCommon> brainControlActorRef;
    private ActorRef<BasicCommon> mongoDBActorRef;
    final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public HttpServer(ActorRef<BasicCommon> brainControlActorRef, ActorRef<BasicCommon> mongoDBActorRef) {
        this.brainControlActorRef = brainControlActorRef;
        this.mongoDBActorRef = mongoDBActorRef;
    }

    public Route createRoute() {
        return concat(
                path("hello", () ->
                        get(() ->
                                complete("<h1>Say hello to akka-http</h1>"))),

//                post(() ->
//                        path("create-model", () ->
//                                entity(Jackson.unmarshaller(Model.class), model -> {
//                                    CompletionStage<Done> futureSaved = getInsertMongoDBDoc(model);
//                                    return onSuccess(futureSaved, done ->
//                                            complete("order created")
//                                    );
//                                }))),

                path("create-model", () ->
                        get(()-> {
                            createModel();
                            return complete("create model cc3200-1111 link succeed");

                        })),

                path("link-device", () ->
                                get(()-> {
                                    linkDevice();
                                    return complete("device link succeed");

                                }))

//                post(() ->
//                        path("test", () ->
//                                entity(Jackson.unmarshaller(Model.class), model -> {
//                                    CompletionStage<Done> futureSaved = test(model);
//                                    return onSuccess(futureSaved, done ->
//                                            complete("order created")
//                                    );
//                                })))



                );
//                post(() ->
//                        patch("create-model", () ->
//                                entity(Jackson.unmarshaller(AbstractModel.class), model -> {
//                                    CompletionStage<Done> futureSaved = getInsertMongoDBDoc(model);
//                                    return onSuccess(futureSaved, done ->
//                                            complete("model created")
//                                    );
//                                } ))
//                );
    }

    private CompletionStage<Done> linkDevice() {
//        System.out.println("2222");
        DeviceModel deviceModel = new DeviceModel();
//        deviceModel.setRealName("cc3200-1");
//        KafkaConfig kafkaConfig = new KafkaConfig();
//        kafkaConfig.setServer("192.168.123.131:9092");
//        kafkaConfig.setGroupId("1");
//        List<String> topics = new ArrayList<>();
//        topics.add("cc3200-1");
//        kafkaConfig.setTopics(topics);
//        deviceModel.setKafkaConfig(kafkaConfig);

//        MqttConfig mqttConfig1 = new MqttConfig();
//        List<String> topic =
//        mqttConfig1.setTopic( "cc3200/humidity");
//        mqttConfig1.setBrokerUrl("tcp://192.168.123.247:1883");
//        mqttConfig1.setClientId("123456");
//        deviceModel.setMqttConfig(mqttConfig1);

//        m.setName("cc3200");
        GetDeviceModelDoc doc = new GetDeviceModelDoc();
        doc.setKey("name");
        doc.setValue("cc3200-1111");
        doc.setConnName("model");
        doc.setCollectionName("model");
        doc.setDeviceModel(deviceModel);
        logger.debug("linkDevice is up....");
        mongoDBActorRef.tell(doc);

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> createModel() {
        InsertMongoDBDoc doc = new InsertMongoDBDoc();
        Map<String,String> map = new HashMap<>();
        AbstractModel model = new AbstractModel();
        model.setName("cc3200");
        model.setNo("1111");
//        Profile profile = new Profile();
//        profile.setProductName("");

        List<Property> properties = new ArrayList<>();
        Property property1 = new Property();
        property1.setName("humidity");
        property1.setMqttTopic("cc3200/1111/humidity");
        properties.add(property1);

        Property property2 = new Property();
        property2.setName("temperature");
        property2.setMqttTopic("cc3200/1111/temperature");
        properties.add(property2);
        model.setProperties(properties);

        List<Event> events = new ArrayList<>();
        Event event1 = new Event();
        event1.setName("light-on");
        event1.setMqttTopic("cc3200/1111/light-on");
        events.add(event1);

        Event event2 = new Event();
        event2.setName("light-off");
        event2.setMqttTopic("cc3200/1111/light-off");
        events.add(event2);

        model.setEvents(events);

        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setModel(model);
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServer("192.168.123.131:9092");
        kafkaConfig.setGroupId("1");
//        List<String> kafkaTopics = new ArrayList<>();
        kafkaConfig.setTopic("cc3200/1111");
//        kafkaConfig.setTopics(kafkaTopics);
        deviceModel.setKafkaConfig(kafkaConfig);

        MqttConfig mqttConfig = new MqttConfig();
        mqttConfig.setBrokerUrl("tcp://192.168.123.247:1883");
        mqttConfig.setClientId("12345");
//        List<String> mqttTopics = new ArrayList<>();
        mqttConfig.setTopic("cc3200/1111/#");
//        mqttTopics.add("cc3200/1111/temperature");
//        mqttConfig.setTopics( mqttTopics);
        deviceModel.setMqttConfig(mqttConfig);


        String str = JSON.toJSONString(deviceModel);

        map.put("name","cc3200-1111");
        map.put("model", str);
        doc.setDocMap(map);
        doc.setConnName("model");
        doc.setCollectionName("model");
//        System.out.println("11111");
        mongoDBActorRef.tell(doc);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> test() {
        InsertMongoDBDoc doc = new InsertMongoDBDoc();
        Map<String,String> map = new HashMap<>();
        map.put("name", "ling");
        doc.setDocMap(map);
        doc.setConnName("test");
        doc.setCollectionName("test");
        System.out.println("11111");
        mongoDBActorRef.tell(doc);
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
