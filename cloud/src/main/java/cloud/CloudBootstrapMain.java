package cloud;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import cloud.actor.BrainControlActor;
import cloud.actor.CloudKafkaConnectInActor;
import cloud.actor.MongoDBConnActor;
import base.model.bean.DeviceModel;
import base.model.bean.BasicCommon;
import base.model.bean.Event;
import base.model.bean.Profile;
import base.model.connect.bean.KafkaConfig;
import cloud.global.GlobalActorRefName;
import cloud.global.GlobalAkkaPara;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:15
 * @description：初始启动类
 */
public class CloudBootstrapMain {
    private static Logger logger = Logger.getLogger(CloudBootstrapMain.class.getName());
    public static void main(String[] args) throws IOException {

        logger.log(Level.INFO, "CloudBootstrapMain start...");
        ActorSystem<Void> system = GlobalAkkaPara.system;
        ActorRef<BasicCommon> brainControlActorRef = system.systemActorOf(BrainControlActor.create(),
                GlobalActorRefName.BRAIN_ACTOR, Props.empty());
        logger.log(Level.INFO, "init BrainControlActor...");
        GlobalAkkaPara.globalActorRefMap.put(GlobalActorRefName.BRAIN_ACTOR, brainControlActorRef);

        ActorRef<BasicCommon> mongoDBActorRef = system.systemActorOf(MongoDBConnActor.create(),
                GlobalActorRefName.MONGODB_CONN_ACTOR, Props.empty());
        logger.log(Level.INFO, "init MongoDBConnActor...");
        GlobalAkkaPara.globalActorRefMap.put(GlobalActorRefName.MONGODB_CONN_ACTOR, mongoDBActorRef);

        ActorRef<BasicCommon> cloudKafkaConnectInActorRef = system.systemActorOf(CloudKafkaConnectInActor.create(),
                GlobalActorRefName.CLOUD_KAFKA_CONNECT_IN_ACTOR, Props.empty());
        logger.log(Level.INFO, "init CloudKafkaConnectInActor...");
        GlobalAkkaPara.globalActorRefMap.put(GlobalActorRefName.CLOUD_KAFKA_CONNECT_IN_ACTOR, cloudKafkaConnectInActorRef);

        httpClientConn(system);
//        CloudKafkaConsumer.init(system);
//        AkkaManagement.get(system).start();
//        ClusterBootstrap.get(system).start();
//        testCC3200(system);
    }


    private static void httpClientConn(ActorSystem<Void> system) throws IOException {
        final Http http = Http.get(system);

        HttpServer server = new HttpServer();
        final CompletionStage<ServerBinding> binding = http.newServerAt("192.168.123.131", 8080)
                .bind(server.createRoute());

        System.out.println("Server online at http://192.168.123.131:8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }
}
