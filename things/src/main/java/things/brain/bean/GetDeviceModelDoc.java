package things.brain.bean;

import lombok.Data;
import things.model.bean.BasicCommon;
import things.model.bean.DeviceModel;


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
