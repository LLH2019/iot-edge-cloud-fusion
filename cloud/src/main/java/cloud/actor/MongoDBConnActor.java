package cloud.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import cloud.global.GlobalActorRefName;
import cloud.global.GlobalAkkaPara;
import com.alibaba.fastjson.JSON;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import cloud.bean.*;
import cloud.util.MongoDBUtil;
import base.model.bean.BasicCommon;
import base.model.bean.DeviceModel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 17:23
 * @description：MongoDB连接Actor
 */
public class MongoDBConnActor extends AbstractBehavior<BasicCommon> {
    private static Logger logger = Logger.getLogger(MongoDBConnActor.class.getName());

    private final Map<String, MongoDatabase> databaseMap = new HashMap<>();
    private final ActorRef<BasicCommon> brainActorRef;

    public MongoDBConnActor(ActorContext<BasicCommon> context) {
        super(context);
        this.brainActorRef = GlobalAkkaPara.globalActorRefMap.get(GlobalActorRefName.BRAIN_ACTOR);
        logger.log(Level.INFO, "MongoDBConnActor init...");
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(CreateNewMongoDBConn.class, this::onHandleMongoDBConnAction)
                .onMessage(InsertMongoDBDoc.class, this::onHandleMongoDbDocAction)
                .onMessage(GetDeviceModelDoc.class, this::onHandleGetDeviceModelDocAction)
                .build();
    }

    private Behavior<BasicCommon> onHandleGetDeviceModelDocAction(GetDeviceModelDoc doc) {
        if(!databaseMap.containsKey(doc.getConnName())) {
            CreateNewMongoDBConn conn = new CreateNewMongoDBConn();
            conn.setConnName(doc.getConnName());
            MongoDBConnConfig config = new MongoDBConnConfig();
            config.setUrl("192.168.123.131");
            config.setPort(27017);
            config.setDbName("model");
            config.setUsername("admin");
            config.setPassword("admin");
            conn.setConfig(config);
            createMongoDbConn(conn);
        }
        MongoDatabase database = databaseMap.get(doc.getConnName());
        MongoCollection<Document> collection = database.getCollection(doc.getCollectionName());
        Bson filter = Filters.eq(doc.getKey(), doc.getValue());
        FindIterable findIterable = collection.find(filter);
        //取出查询到的第一个文档
        Document document = (Document) findIterable.first();


        DeviceModel model = JSON.parseObject(document.get("model").toString(), DeviceModel.class);

        logger.log(Level.INFO, "MongoDBConnActor get model " +  model.toString());
        brainActorRef.tell(model);
        return this;
    }


    private Behavior<BasicCommon> onHandleMongoDbDocAction(InsertMongoDBDoc doc) {
        System.out.println("2222");
        if(!databaseMap.containsKey(doc.getConnName())) {
            CreateNewMongoDBConn conn = new CreateNewMongoDBConn();
            conn.setConnName(doc.getConnName());
            MongoDBConnConfig config = new MongoDBConnConfig();
            config.setUrl("192.168.123.131");
            config.setPort(27017);
            config.setDbName("model");
            config.setUsername("admin");
            config.setPassword("admin");
            conn.setConfig(config);
            createMongoDbConn(conn);
        }
//        System.out.println("2222");
        MongoDatabase database = databaseMap.get(doc.getConnName());
        MongoCollection<Document> collection = database.getCollection(doc.getCollectionName());
        //要插入的数据
        Document document = new Document();
        for (Map.Entry<String, String> entry : doc.getDocMap().entrySet()) {
            document.append(entry.getKey(), entry.getValue());
        }
        //插入一个文档
        collection.insertOne(document);
        return this;
    }

    private Behavior<BasicCommon> onHandleMongoDBConnAction(CreateNewMongoDBConn conn) {
        createMongoDbConn(conn);
        return this;
    }

    private void createMongoDbConn(CreateNewMongoDBConn conn) {
        if(!databaseMap.containsKey(conn.getConnName())) {
            MongoDatabase database = MongoDBUtil.getConnect(conn.getConfig());
            databaseMap.put(conn.getConnName(), database);
        }
    }



    public static Behavior<BasicCommon> create() {
        return Behaviors.setup(context -> new MongoDBConnActor(context));
    }
}
