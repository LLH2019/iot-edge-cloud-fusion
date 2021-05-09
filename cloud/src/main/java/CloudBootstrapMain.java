import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import cloud.HttpServer;
import cloud.actor.BrainControlActor;
import cloud.actor.MongoDBConnActor;
import base.model.bean.DeviceModel;
import base.model.bean.BasicCommon;
import base.model.bean.Event;
import base.model.bean.Profile;
import base.model.connect.bean.KafkaConfig;
import cloud.connect.CloudKafkaConsumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:15
 * @description：初始启动类
 */
public class CloudBootstrapMain {
    public static void main(String[] args) throws IOException {


        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "bootstrap");
        httpClientConn(system);
//        CloudKafkaConsumer.init(system);
//        AkkaManagement.get(system).start();
//        ClusterBootstrap.get(system).start();
//        testCC3200(system);
    }

    private static void testCC3200(ActorSystem<Void> system) {
        Profile profile = new Profile();
        profile.setProductName("cc3200-1");

        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setName("humidity");

        events.add(event);
        DeviceModel model = new DeviceModel();
//        model.setProfile(profile);
//        model.setEvents(events);

    }

    private static void httpClientConn(ActorSystem<Void> system) throws IOException {
        final Http http = Http.get(system);
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServer("192.168.123.131:9092");
        kafkaConfig.setGroupId("1");
//        List<String> list = new ArrayList<>();
//        list.add("llh.brain-1");
//        kafkaConfig.setTopic("brain");
        ActorRef<BasicCommon> brainControlActorRef = system.systemActorOf(BrainControlActor.create(kafkaConfig, system),
                "brain-control", Props.empty());

        ActorRef<BasicCommon> mongoDBActorRef = system.systemActorOf(MongoDBConnActor.create(brainControlActorRef),
                "mongoDB-conn", Props.empty());

        HttpServer server = new HttpServer(brainControlActorRef, mongoDBActorRef);
        final CompletionStage<ServerBinding> binding = http.newServerAt("192.168.123.131", 8080)
                .bind(server.createRoute());

        System.out.println("Server online at http://192.168.123.131:8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }
}
