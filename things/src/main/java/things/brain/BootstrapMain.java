package things.brain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import things.brain.actor.BrainControlActor;
import things.brain.actor.MongoDBConnActor;
import things.model.bean.DeviceModel;
import things.model.bean.BasicCommon;
import things.model.bean.Event;
import things.model.bean.Profile;
import things.model.connect.bean.KafkaConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:15
 * @description：初始启动类
 */
public class BootstrapMain {
    public static void main(String[] args) throws IOException {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "bootstrap");
        httpClientConn(system);
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
        List<String> list = new ArrayList<>();
        list.add("llh.brain-1");
        kafkaConfig.setTopics(list);
        ActorRef<BasicCommon> brainControlActorRef = system.systemActorOf(BrainControlActor.create(kafkaConfig),
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
