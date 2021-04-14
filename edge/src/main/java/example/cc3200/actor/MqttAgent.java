package example.cc3200.actor;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class MqttAgent extends AbstractBehavior<MqttAgent.Command> {
    public MqttAgent(ActorContext<Command> context) {
        super(context);
    }



    @Override
    public Receive<Command> createReceive() {
        return null;
    }

    interface Command{}




}
