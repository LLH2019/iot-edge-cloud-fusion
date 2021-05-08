package cloud.bean;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 17:30
 * @description：MongoDB连接相关配置
 */
@Data
public class MongoDBConnConfig {
    private String url;
    private int port;
    private String username;
    private String password;
    private String dbName;

}
