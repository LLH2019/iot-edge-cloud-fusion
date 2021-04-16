package things.model.actor;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import things.model.bean.BasicCommon;
import things.model.connect.DownConnectIn;
import things.model.connect.DownConnectOut;
import things.model.connect.UpConnectOut;
import things.model.connect.UpConnectIn;


/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：抽象基础类
 */
public class AbstractBasicActor extends AbstractBehavior<BasicCommon> implements DownConnectIn, UpConnectOut, DownConnectOut, UpConnectIn {

    public AbstractBasicActor(ActorContext<BasicCommon> context) {
        super(context);
    }

    @Override
    public void DownConnectIn() {

    }

    @Override
    public void connectOut() {

    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return null;
    }

    @Override
    public void downConnectOut() {

    }

    @Override
    public void upConnectIn() {

    }
}
