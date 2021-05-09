//package example.cc3200;
//
//import akka.actor.typed.ActorRef;
//import akka.actor.typed.ActorSystem;
//import akka.actor.typed.Behavior;
//import akka.actor.typed.Props;
//import akka.actor.typed.javadsl.Behaviors;
//import akka.management.cluster.bootstrap.ClusterBootstrap;
//import akka.management.javadsl.AkkaManagement;
//import example.cc3200.actor.CC3200ServerActor;
//import example.cc3200.kafka.CommonKafkaConsumerDemo;
//import example.cc3200.kafka.KafkaConsumerDemo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class Main {
//    private static final Logger logger = LoggerFactory.getLogger(Main.class);
//
//    public static void main(String[] args) {
//        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "ShoppingAnalyticsService");
//        try {
//            init(system);
//        } catch (Exception e) {
//            logger.error("Terminating due to initialization failure.", e);
//            system.terminate();
//        }
//    }
//
//    public static void init(ActorSystem<Void> system) {
////        AkkaManagement.get(system).start();
////        ClusterBootstrap.get(system).start();
//
//        ActorRef<CC3200ServerActor.Command> serverActorRef =system.systemActorOf(CC3200ServerActor.create(),
//                "CC3200ServerActor", Props.empty());
//
//        CommonKafkaConsumerDemo.init(serverActorRef);
//
////        KafkaConsumerDemo.init(system);
//    }
//}
