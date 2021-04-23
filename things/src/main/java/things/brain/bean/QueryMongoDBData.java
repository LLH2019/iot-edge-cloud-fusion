package things.brain.bean;

import lombok.Data;
import org.bson.Document;
import things.base.DataType;
import things.model.bean.BasicCommon;

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
