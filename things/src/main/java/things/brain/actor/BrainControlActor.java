package things.brain.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import things.brain.bean.EdgeDevice;
import things.brain.bean.NewDeviceConn;
import things.controller.actor.CloudControlActor;
import things.controller.actor.CloudControlActorKafkaInAndOut;
import things.model.bean.BasicCommon;

import java.util.List;
import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:01
 * @description：最高控制的actor
 */
public class BrainControlActor extends AbstractBehavior<BasicCommon> {

    private List<EdgeDevice> edgeDevices;

    ActorRef<BasicCommon> httpClientActorRef;
    Map<String, ActorRef<BasicCommon>> refMaps;

    public BrainControlActor(ActorContext<BasicCommon> context) {
        super(context);
        init();
    }

    private void init() {
        httpClientActorRef = getContext().spawn(HttpClientActor.create(), "httpClientActor");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(NewDeviceConn.class, this::onNewDeviceConnAction)


                .build();
    }

    private Behavior<BasicCommon> onNewDeviceConnAction(NewDeviceConn conn) {
        ActorRef<BasicCommon>  ref = getContext().spawn(CloudControlActorKafkaInAndOut.create(conn.getKafkaConfig()), conn.getName());
//        ref.tell()
        refMaps.put(conn.getName(), ref);

        return this;
    }


}
