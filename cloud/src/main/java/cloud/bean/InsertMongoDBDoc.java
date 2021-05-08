package cloud.bean;

import lombok.Data;
import base.model.bean.BasicCommon;

import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 17:56
 * @description：mongoDB 插入消息
 */
@Data
public class InsertMongoDBDoc implements BasicCommon {
    private String connName;
    private String collectionName;
    private Map<String, String> docMap;
}
