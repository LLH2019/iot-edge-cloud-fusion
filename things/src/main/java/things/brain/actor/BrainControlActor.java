package things.brain.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import things.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:01
 * @description：最高控制的actor
 */
public class BrainControlActor extends AbstractBehavior<BasicCommon> {

    ActorRef<BasicCommon> httpClientActorRef;

    public BrainControlActor(ActorContext<BasicCommon> context) {
        super(context);
        init();
    }

    private void init() {
        httpClientActorRef = getContext().spawn(HttpClientActor.create(), "httpClientActor");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return null;
    }


}
