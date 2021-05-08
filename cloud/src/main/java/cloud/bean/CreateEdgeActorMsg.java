package cloud.bean;

import lombok.Data;
import base.model.bean.DeviceModel;
import base.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/20 22:37
 * @description：边缘处接收云端消息进行actor构建
 */
@Data
public class CreateEdgeActorMsg implements BasicCommon {
    private DeviceModel model;
//    private
}
