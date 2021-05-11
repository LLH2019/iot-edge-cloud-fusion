package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;
import cloud.global.GlobalActorRefName;
import cloud.global.GlobalAkkaPara;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:01
 * @description：最高控制的actor
 */
public class BrainControlActor extends AbstractBehavior<BasicCommon> {

    private static Logger logger = Logger.getLogger(BrainControlActor.class.getName());
    private final ActorRef<BasicCommon> kafkaConnectInActorRef;
    private final Map<String, ActorRef<BasicCommon>> cloudControlRefMaps = new HashMap<>();
    private final ActorSystem<?> system;

    public BrainControlActor(ActorContext<BasicCommon> context) {
        super(context);
        this.system = GlobalAkkaPara.system;
        this.kafkaConnectInActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.CLOUD_KAFKA_CONNECT_IN_ACTOR);
        logger.log(Level.INFO, "BrainControlActor init...");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(DeviceModel.class, this::onHandleDeviceLink)
                .build();
    }

    private Behavior<BasicCommon> onHandleDeviceLink(DeviceModel model) {
        logger.log(Level.INFO, "BrainControlActor handle device link..." + model);

        String realName = model.getModel().getName() + "-" +  model.getModel().getNo();
        logger.log(Level.INFO, "BrainControlActor spawn device..." + realName + model);
        ActorRef<BasicCommon>  ref = getContext().spawn(DeviceCloudActor.create(model), realName);
        cloudControlRefMaps.put(realName, ref);
        return this;
    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new BrainControlActor(context));
    }

}
