package things.model.connect.bean;

import lombok.Data;
import things.model.bean.BasicCommon;

import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/21 15:12
 * @description：订阅消息
 */
@Data public class SubscribeTopic implements BasicCommon {
    private List<String> topics;
}
