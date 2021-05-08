package cloud.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import cloud.bean.MongoDBConnConfig;

import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 17:27
 * @description：mongodb工具类
 */
public class MongoDBUtil {

    public static MongoDatabase getConnect(MongoDBConnConfig config){
        List<ServerAddress> adds = new ArrayList<>();
        //ServerAddress()两个参数分别为 服务器地址 和 端口
        ServerAddress serverAddress = new ServerAddress(config.getUrl(), config.getPort());
        adds.add(serverAddress);

        List<MongoCredential> credentials = new ArrayList<>();
        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(config.getUsername(), config.getDbName(), config.getPassword().toCharArray());
        credentials.add(mongoCredential);

        //通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(adds, credentials);

        //连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(config.getDbName());

        //返回连接数据库对象
        return mongoDatabase;
    }

}
