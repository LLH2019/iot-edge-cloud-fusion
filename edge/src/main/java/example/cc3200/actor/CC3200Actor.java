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
import example.cc3200.kafka.CommonKafkaPublishDemo;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;


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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = df.format(new Date()) + "-temperature";
        CommonKafkaPublishDemo.sendMessageForgetResult(desc.getName(), key, tem.getValue());
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
