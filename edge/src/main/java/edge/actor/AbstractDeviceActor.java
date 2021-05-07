package edge.actor;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.connect.DownConnectIn;
import base.model.connect.DownConnectOut;
import base.model.connect.UpConnectOut;
import base.model.connect.UpConnectIn;


/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：抽象基础类
 */
public class AbstractDeviceActor extends AbstractBehavior<BasicCommon> implements DownConnectIn, UpConnectOut, DownConnectOut, UpConnectIn {

    public AbstractDeviceActor(ActorContext<BasicCommon> context) {
        super(context);
    }

    @Override
    public void downConnectIn() {

    }

    @Override
    public void upConnectOut() {

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
