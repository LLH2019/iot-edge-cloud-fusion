package cloud.bean;

import base.type.DataType;
import lombok.Data;
import org.bson.Document;
import base.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 20:11
 * @description：查询MongoDB 数据
 */
@Data
public class QueryMongoDBData implements BasicCommon {
    private DataType type;
    private Document doc;
}
