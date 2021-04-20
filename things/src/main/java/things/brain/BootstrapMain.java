package things.brain;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:15
 * @description：初始启动类
 */
public class BootstrapMain {
    public static void main(String[] args) throws IOException {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "bootstrap");
        httpClientConn(system);
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
