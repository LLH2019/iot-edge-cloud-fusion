package cloud.bean;

import lombok.Data;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;


/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 18:07
 * @description：得到相关的第一个查询对象
 */
@Data
public class GetDeviceModelDoc implements BasicCommon {
    private String connName;
    private String collectionName;
    private String key;
    private String value;
    private DeviceModel deviceModel;
}
