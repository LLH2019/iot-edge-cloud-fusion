package cloud.bean;

import lombok.Data;
import base.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 17:39
 * @description：新建MongoDB连接
 */
@Data
public class CreateNewMongoDBConn implements BasicCommon {
    private String connName;
    private MongoDBConnConfig config;
}
