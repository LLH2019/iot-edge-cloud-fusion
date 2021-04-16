package things.controller.actor;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import things.controller.bean.Device;
import things.controller.bean.EdgeActor;
import things.model.bean.BasicCommon;
import things.model.connect.DownConnectIn;
import things.model.connect.UpConnectOut;
import things.model.connect.bean.KafkaMsg;

import java.util.List;
import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 12:12
 * @description：控制actor的运行
 */
public class BrainControlActor extends AbstractBehavior<BasicCommon> implements DownConnectIn, UpConnectOut {
    private Map<Device, List<EdgeActor>> deviceListMap;
    private Map<Device, EdgeActor> onlineDeviceMap;

    public BrainControlActor(ActorContext<BasicCommon> context) {
        super(context);
        init();
    }

    public void init() {
//        new Thread(new CheckRunningSituation());


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
