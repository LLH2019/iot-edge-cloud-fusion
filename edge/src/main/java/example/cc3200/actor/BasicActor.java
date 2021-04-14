package example.cc3200.actor;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class BasicActor extends AbstractBehavior<Void> {
    public BasicActor(ActorContext<Void> context) {
        super(context);
    }


    @Override
    public Receive<Void> createReceive() {
        return null;
    }
}
