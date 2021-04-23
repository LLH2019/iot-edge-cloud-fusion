package things.brain.bean;

import lombok.Data;
import things.model.bean.BasicCommon;

import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 18:07
 * @description：得到相关的第一个查询对象
 */
@Data
public class GetFirstMongoDBDoc implements BasicCommon {
    private String connName;
    private String collectionName;
    private String key;
    private String value;
}
