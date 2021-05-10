package cloud;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

import cloud.front.GetKafkaMsg;
import cloud.front.TotalInfo;
import cloud.global.GlobalActorRefName;
import cloud.global.GlobalAkkaPara;
import com.alibaba.fastjson.JSON;
import cloud.bean.GetDeviceModelDoc;
import cloud.bean.InsertMongoDBDoc;
import base.model.bean.*;

import static akka.http.javadsl.server.PathMatchers.segment;
/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:22
 * @description：http server
 */
public class HttpServer extends AllDirectives {
    private static java.util.logging.Logger logger = Logger.getLogger(HttpServer.class.getName());
    private final ActorRef<BasicCommon> brainControlActorRef;
    private final ActorRef<BasicCommon> mongoDBActorRef;
    private final ActorSystem<?> system;

    public HttpServer() {
        this.brainControlActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.BRAIN_ACTOR);
        this.mongoDBActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.MONGODB_CONN_ACTOR);
        this.system = GlobalAkkaPara.system;
        logger.log(Level.INFO, "HttpServer init...");
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
                get(()->
                        pathPrefix("create-model-cc3200", ()->
                                path(segment(), (String no)-> {
                                    final CompletionStage<Done> futureStage = createCC3200Model(no);
                                    return onSuccess(futureStage, done -> complete("success create cc3200 model" + no));
                                }))),

                get(()->
                        pathPrefix("link-device-no", ()->
                                path(segment(), (String no)-> {
                                    final CompletionStage<Done> futureStage = linkDeviceByNo(no);
                                    return onSuccess(futureStage, done -> complete("success link device... " + no));
                                }))),

                get(()->
                        pathPrefix("publish-event", ()->
                                path(segment(), (String event)-> {
                                    final CompletionStage<Done> futureStage = publishEventToDevice(event);
                                    return onSuccess(futureStage, done -> complete("success link device... "));
                                }))),

//                path("create-model", () ->
//                        get(()-> {
//                            createModel();
//                            return complete("create model cc3200-1111 link succeed");
//
//                        })),

                path("link-device-all", () ->
                                get(()-> {
                                    linkDevice();
                                    return complete("device link succeed");

                                })),

                path("get-kafka-msg", () ->
                        get(()-> {
//                            String result = "";
//                            for()
                            return complete("return kafka msg: " + GetKafkaMsg.kafkaMsg);
//                            CompletionStage<Done> futureSaved = getKafkaMsg();
//                            return onSuccess(futureSaved, done ->
//                                            complete("order created" + futureSaved.toString())
//                                    );

                        })),

                path("get-device-info", () ->
                        get(()-> {
//                            String result = "";
//                            for()
                            return complete("return kafka msg: " + TotalInfo.deviceNums + " "
                                    + TotalInfo.deviceSets + " " +TotalInfo.deviceInfoMap);
//                            CompletionStage<Done> futureSaved = getKafkaMsg();
//                            return onSuccess(futureSaved, done ->
//                                            complete("order created" + futureSaved.toString())
//                                    );

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

    private CompletionStage<Done> publishEventToDevice(String event) {


        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> linkDeviceByNo(String no) {
        DeviceModel deviceModel = new DeviceModel();

        GetDeviceModelDoc doc = new GetDeviceModelDoc();
        doc.setKey("name");
        doc.setValue("cc3200-" + no);
        doc.setConnName("model");
        doc.setCollectionName("model");
        doc.setDeviceModel(deviceModel);
        logger.log(Level.INFO, "device cc3200-" + no + " is up....");
        mongoDBActorRef.tell(doc);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> createCC3200Model(String no) {
        System.out.println("create model " + no);

        InsertMongoDBDoc doc = new InsertMongoDBDoc();
        Map<String,String> map = new HashMap<>();
        AbstractModel model = new AbstractModel();
        model.setName("cc3200");
        model.setNo(no);

        List<Property> properties = new ArrayList<>();
        Property property1 = new Property();
        property1.setName("humidity");
        properties.add(property1);

        Property property2 = new Property();
        property2.setName("temperature");
        properties.add(property2);
        model.setProperties(properties);

        List<Event> events = new ArrayList<>();
        Event event1 = new Event();
        event1.setName("light-on");
        events.add(event1);

        Event event2 = new Event();
        event2.setName("light-off");
        events.add(event2);

        model.setEvents(events);

        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setModel(model);

        String str = JSON.toJSONString(deviceModel);

        map.put("name","cc3200-" + no);
        map.put("model", str);
        doc.setDocMap(map);
        doc.setConnName("model");
        doc.setCollectionName("model");
//        System.out.println("11111");
        mongoDBActorRef.tell(doc);
        return CompletableFuture.completedFuture(Done.getInstance());
    }





    private CompletionStage<Done> linkDevice() {
        DeviceModel deviceModel = new DeviceModel();

        GetDeviceModelDoc doc = new GetDeviceModelDoc();
        doc.setKey("name");
        doc.setValue("cc3200-1111");
        doc.setConnName("model");
        doc.setCollectionName("model");
        doc.setDeviceModel(deviceModel);
        logger.log(Level.INFO, "linkDevice is up....");
        mongoDBActorRef.tell(doc);

        DeviceModel deviceModel2 = new DeviceModel();
        GetDeviceModelDoc doc2 = new GetDeviceModelDoc();
        doc2.setKey("name");
        doc2.setValue("cc3200-3333");
        doc2.setConnName("model");
        doc2.setCollectionName("model");
        doc2.setDeviceModel(deviceModel2);
        logger.log(Level.INFO, "linkDevice is up....");
        mongoDBActorRef.tell(doc2);

        return CompletableFuture.completedFuture(Done.getInstance());
    }

//    private CompletionStage<Done> createModel() {
//        InsertMongoDBDoc doc = new InsertMongoDBDoc();
//        Map<String,String> map = new HashMap<>();
//        AbstractModel model = new AbstractModel();
//        model.setName("cc3200");
//        model.setNo("1111");
////        Profile profile = new Profile();
////        profile.setProductName("");
//
//        List<Property> properties = new ArrayList<>();
//        Property property1 = new Property();
//        property1.setName("humidity");
////        property1.setMqttTopic("cc3200/1111/humidity");
//        properties.add(property1);
//
//        Property property2 = new Property();
//        property2.setName("temperature");
////        property2.setMqttTopic("cc3200/1111/temperature");
//        properties.add(property2);
//        model.setProperties(properties);
//
//        List<Event> events = new ArrayList<>();
//        Event event1 = new Event();
//        event1.setName("light-on");
////        event1.setMqttTopic("cc3200/1111/light-on");
//        events.add(event1);
//
//        Event event2 = new Event();
//        event2.setName("light-off");
////        event2.setMqttTopic("cc3200/1111/light-off");
//        events.add(event2);
//
//        model.setEvents(events);
//
//        DeviceModel deviceModel = new DeviceModel();
//        deviceModel.setModel(model);
////        KafkaConfig kafkaConfig = new KafkaConfig();
////        kafkaConfig.setServer("192.168.123.131:9092");
////        kafkaConfig.setGroupId("1");
//////        List<String> kafkaTopics = new ArrayList<>();
////        kafkaConfig.setTopic("cc3200/1111");
//////        kafkaConfig.setTopics(kafkaTopics);
////        deviceModel.setKafkaConfig(kafkaConfig);
//
////        MqttConfig mqttConfig = new MqttConfig();
////        mqttConfig.setBrokerUrl("tcp://192.168.123.247:1883");
////        mqttConfig.setClientId("12345");
//////        List<String> mqttTopics = new ArrayList<>();
////        mqttConfig.setTopic("cc3200/1111/#");
//////        mqttTopics.add("cc3200/1111/temperature");
//////        mqttConfig.setTopics( mqttTopics);
////        deviceModel.setMqttConfig(mqttConfig);
//
//
//        String str = JSON.toJSONString(deviceModel);
//
//        map.put("name","cc3200-1111");
//        map.put("model", str);
//        doc.setDocMap(map);
//        doc.setConnName("model");
//        doc.setCollectionName("model");
////        System.out.println("11111");
//        mongoDBActorRef.tell(doc);
//        return CompletableFuture.completedFuture(Done.getInstance());
//    }
//
//    private CompletionStage<Done> test() {
//        InsertMongoDBDoc doc = new InsertMongoDBDoc();
//        Map<String,String> map = new HashMap<>();
//        map.put("name", "ling");
//        doc.setDocMap(map);
//        doc.setConnName("test");
//        doc.setCollectionName("test");
//        System.out.println("11111");
//        mongoDBActorRef.tell(doc);
//        return CompletableFuture.completedFuture(Done.getInstance());
//    }
}
