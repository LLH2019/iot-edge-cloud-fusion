package things.brain.bean;

import lombok.Data;
import things.model.bean.AbstractModel;
import things.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 22:37
 * @description：边缘处接收云端消息进行actor构建
 */
@Data
public class CreateEdgeActorMsg implements BasicCommon {
    private AbstractModel model;
//    private
}
