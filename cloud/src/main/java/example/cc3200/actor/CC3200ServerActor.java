package example.cc3200.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import example.cc3200.bean.KafkaData;

public class CC3200ServerActor extends AbstractBehavior<CC3200ServerActor.Command> {

    public CC3200ServerActor(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {

        return newReceiveBuilder()
                .onMessage(KafkaData.class, this::onGetKafkaData)

                .build();
    }

    private Behavior<Command> onGetKafkaData(KafkaData data) {

        persistData(data);
        return this;
    }

    private void persistData(KafkaData data) {
        System.out.println("CC3200ServerActor " + data.getTopic() + ":" + data.getKey() + ":" + data.getValue());
    }

    public interface Command{}

    public static Behavior<Command> create() {
        return Behaviors.setup(context -> new CC3200ServerActor(context));
    }



}
