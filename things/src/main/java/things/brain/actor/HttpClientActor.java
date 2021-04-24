package things.brain.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import things.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:07
 * @description：http 客户端交互actor
 */
public class HttpClientActor extends AbstractBehavior<BasicCommon> {
    public HttpClientActor(ActorContext<BasicCommon> context) {
        super(context);
        init();
    }

    private void init() {
//        Http http = Http.get()

    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder().build();
    }

    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new HttpClientActor(context));
    }
}
