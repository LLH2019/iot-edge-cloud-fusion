package things.brain;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import things.model.bean.AbstractModel;
import things.model.bean.Event;
import things.model.bean.Profile;

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
        testCC3200(system);
    }

    private static void testCC3200(ActorSystem<Void> system) {
        Profile profile = new Profile();
        profile.setProductName("cc3200-1");

        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setName("humidity");

        events.add(event);
        AbstractModel model = new AbstractModel();
        model.setProfile(profile);
        model.setEvents(events);

    }

    private static void httpClientConn(ActorSystem<Void> system) throws IOException {
        final Http http = Http.get(system);
        HttpServer server = new HttpServer();
        final CompletionStage<ServerBinding> binding = http.newServerAt("localhost", 8080)
                .bind(server.createRoute());

        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }
}
