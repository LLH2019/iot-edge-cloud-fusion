package things.brain;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import akka.http.javadsl.marshallers.jackson.Jackson;
import com.alibaba.fastjson.JSON;
import things.brain.bean.GetDeviceModelDoc;
import things.brain.bean.InsertMongoDBDoc;
import things.client.bean.Model;
import things.model.bean.*;
import things.model.connect.bean.KafkaConfig;
import things.model.connect.bean.MqttConfig;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:22
 * @description：http server
 */
public class HttpServer extends AllDirectives {
    private ActorRef<BasicCommon> brainControlActorRef;
    private ActorRef<BasicCommon> mongoDBActorRef;

    public HttpServer(ActorRef<BasicCommon> brainControlActorRef, ActorRef<BasicCommon> mongoDBActorRef) {
        this.brainControlActorRef = brainControlActorRef;
        this.mongoDBActorRef = mongoDBActorRef;
    }

    public Route createRoute() {
        return concat(
                path("hello", () ->
                        get(() ->
                                complete("<h1>Say hello to akka-http</h1>"))),

                post(() ->
                        path("create-model", () ->
                                entity(Jackson.unmarshaller(Model.class), model -> {
                                    CompletionStage<Done> futureSaved = getInsertMongoDBDoc(model);
                                    return onSuccess(futureSaved, done ->
                                            complete("order created")
                                    );
                                }))),
                post(() ->
                        path("link-device", () ->
                                entity(Jackson.unmarshaller(Model.class), model -> {
                                    CompletionStage<Done> futureSaved = linkDevice(model);
                                    return onSuccess(futureSaved, done ->
                                            complete("device link succeed")
                                    );
                                }))),

                post(() ->
                        path("test", () ->
                                entity(Jackson.unmarshaller(Model.class), model -> {
                                    CompletionStage<Done> futureSaved = test(model);
                                    return onSuccess(futureSaved, done ->
                                            complete("order created")
                                    );
                                })))



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

    private CompletionStage<Done> linkDevice(Model m) {
        DeviceModel deviceModel = new DeviceModel();
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServer("192.168.123.131:9092");
        kafkaConfig.setGroupId("1");
        List<String> topics = new ArrayList<>();
        topics.add("cc3200-1");
        kafkaConfig.setTopics(topics);
        deviceModel.setKafkaConfig(kafkaConfig);

        MqttConfig mqttConfig1 = new MqttConfig();
        mqttConfig1.setTopic( "cc3200/humidity");
        mqttConfig1.setBrokerUrl("tcp://192.168.123.247:1883");
        mqttConfig1.setClientId("123456");
        deviceModel.setMqttConfig(mqttConfig1);

        m.setName("cc3200");
        GetDeviceModelDoc doc = new GetDeviceModelDoc();
        doc.setKey("name");
        doc.setValue("cc3200");
        doc.setConnName("model");
        doc.setCollectionName("model");
        doc.setDeviceModel(deviceModel);
        mongoDBActorRef.tell(doc);

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> getInsertMongoDBDoc(Model m) {
        InsertMongoDBDoc doc = new InsertMongoDBDoc();
        Map<String,String> map = new HashMap<>();
        AbstractModel model = new AbstractModel();
        model.setName("cc3200");
        Profile profile = new Profile();
//        profile.setProductName("");

        List<Property> properties = new ArrayList<>();
        Property property1 = new Property();
        property1.setName("humidity");
        properties.add(property1);

        model.setProperties(properties);

        String str = JSON.toJSONString(model);

        map.put("cc3200", str);
        doc.setDocMap(map);
        doc.setConnName("model");
        doc.setCollectionName("model");
//        System.out.println("11111");
        mongoDBActorRef.tell(doc);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> test(Model model) {
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
