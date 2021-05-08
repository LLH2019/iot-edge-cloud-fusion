package cloud.actor;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import base.model.bean.BasicCommon;
import base.model.connect.DownConnectIn;
import base.model.connect.UpConnectOut;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 12:12
 * @description：控制actor的运行
 */
public class AbstractCloudControlActor extends AbstractBehavior<BasicCommon> implements DownConnectIn, UpConnectOut {
//    private Map<Device, List<EdgeActor>> deviceListMap;
//    private Map<Device, EdgeActor> onlineDeviceMap;

    public AbstractCloudControlActor(ActorContext<BasicCommon> context) {
        super(context);
    }



    @Override
    public Receive<BasicCommon> createReceive() {
        return null;
    }

    @Override
    public void downConnectIn() {

    }

    @Override
    public void upConnectOut() {

    }


    public interface Command{};

}
