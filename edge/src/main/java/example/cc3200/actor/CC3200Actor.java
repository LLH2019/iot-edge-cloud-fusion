package example.cc3200.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.Behaviors;
import example.cc3200.bean.CC3200Desc;
import example.cc3200.bean.Command;
import lombok.Data;


public class CC3200Actor extends AbstractBehavior<Command> {

    public final CC3200Desc desc;

    public CC3200Actor(ActorContext<Command> context, CC3200Desc desc) {
        super(context);
        this.desc = desc;
    }


    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(TemperatureUpload.class, this::onTemperatureUploadAction)

                .build();
    }

    private Behavior<Command> onTemperatureUploadAction(TemperatureUpload tem) {
        handleTemperature(tem);

        return this;
    }

    private void handleTemperature(TemperatureUpload tem) {
        System.out.println(this.desc.getName() + " " + tem.getValue());
    }


    public static Behavior<Command> create(CC3200Desc desc) {
        return Behaviors.setup(context -> new CC3200Actor(context, desc));
    }




    @Data
    public static class TemperatureUpload implements Command {
        private final String value;
        public TemperatureUpload(String value) {
            this.value = value;
        }
    }
}
