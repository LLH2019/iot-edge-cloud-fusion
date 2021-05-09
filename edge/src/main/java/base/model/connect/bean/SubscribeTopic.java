package base.model.connect.bean;

import akka.actor.typed.ActorRef;
import base.model.bean.BasicCommon;
import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 15:12
 * @description：订阅消息
 */
@Data
public class SubscribeTopic implements BasicCommon {
    private ActorRef<BasicCommon> ref;
    private String topic;
}
