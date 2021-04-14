package example.cc3200.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class MqttAgent extends AbstractBehavior<MqttAgent.Command> {

    public final Map<String, ActorRef<MqttActor.Command>> MqttActorMap = new HashMap<>();


    public MqttAgent(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(context -> new MqttAgent(context));
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(AddMqttActor.class, this::onAddMqttActorAction)

                .build();
    }

    private Behavior<Command> onAddMqttActorAction(AddMqttActor actor) {
        if(!MqttActorMap.containsKey(actor.id)) {
            MqttActorMap.put(actor.id, actor.ref);
        }
        return this;
    }

    public interface Command{}

    @Data
    public static class AddMqttActor implements Command {
        private final String id;
        private final ActorRef<MqttActor.Command> ref;




        public AddMqttActor(String id, ActorRef<MqttActor.Command> ref) {
            this.id = id;
            this.ref = ref;
        }
    }

}
