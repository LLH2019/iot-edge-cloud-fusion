package things.brain.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import things.base.DataType;
import things.brain.bean.CreateNewMongoDBConn;
import things.brain.bean.GetFirstMongoDBDoc;
import things.brain.bean.InsertMongoDBDoc;
import things.brain.bean.QueryMongoDBData;
import things.brain.util.MongoDBUtil;
import things.model.bean.BasicCommon;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/23 17:23
 * @description：MongoDB连接Actor
 */
public class MongoDBConnActor extends AbstractBehavior<BasicCommon> {
    private Map<String, MongoDatabase> databaseMap = new HashMap<>();
    private ActorRef<BasicCommon> brainActorRef;

    public MongoDBConnActor(ActorContext<BasicCommon> context) {
        super(context);
    }

    @Override
    public Receive<BasicCommon> createReceive() {
        return newReceiveBuilder()
                .onMessage(CreateNewMongoDBConn.class, this::onHandleMongoDBConnAction)
                .onMessage(InsertMongoDBDoc.class, this::onHandleMongoDbDocAction)
                .onMessage(GetFirstMongoDBDoc.class, this::onHandleGetMongoDBDocAction)
                .build();
    }

    private Behavior<BasicCommon> onHandleGetMongoDBDocAction(GetFirstMongoDBDoc doc) {
        MongoDatabase database = databaseMap.get(doc.getConnName());
        MongoCollection<Document> collection = database.getCollection(doc.getCollectionName());
        Bson filter = Filters.eq(doc.getKey(), doc.getValue());
        FindIterable findIterable = collection.find(filter);
        //取出查询到的第一个文档
        Document document = (Document) findIterable.first();

        QueryMongoDBData queryMongoDBData = new QueryMongoDBData();
        queryMongoDBData.setType(DataType.NEW_MODEl);
        queryMongoDBData.setDoc(document);

        brainActorRef.tell(queryMongoDBData);
        return this;
    }


    private Behavior<BasicCommon> onHandleMongoDbDocAction(InsertMongoDBDoc doc) {
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
        if(!databaseMap.containsKey(conn.getConnName())) {
            MongoDatabase database = MongoDBUtil.getConnect(conn.getConfig());
            databaseMap.put(conn.getConnName(), database);
        }
        return this;
    }
}
