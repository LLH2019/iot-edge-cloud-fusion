package things.brain;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import akka.http.javadsl.marshallers.jackson.Jackson;
import jnr.ffi.annotations.In;
import things.brain.bean.InsertMongoDBDoc;
import things.client.bean.CreateNewDeviceModel;
import things.client.bean.Model;
import things.model.bean.AbstractModel;
import things.model.bean.BasicCommon;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:22
 * @description：http server
 */
public class HttpServer extends AllDirectives {
    private ActorRef<BasicCommon> brainControlActorRef;
    private ActorRef<BasicCommon> mongoDBActorRef;

    public HttpServer(ActorRef<BasicCommon> ref, ActorRef<BasicCommon> mongoDBActorRef) {
        this.brainControlActorRef = ref;
        this.mongoDBActorRef = ref;
    }

    public Route createRoute() {
        return concat(
                path("hello", () ->
                        get(() ->
                                complete("<h1>Say hello to akka-http</h1>"))),

                post(() ->
                        path("create-model", () ->
                                entity(Jackson.unmarshaller(AbstractModel.class), model -> {
                                    CompletionStage<Done> futureSaved = getInsertMongoDBDoc(model);
                                    return onSuccess(futureSaved, done ->
                                            complete("order created")
                                    );
                                }))),


                post(() ->
                        path("test", () ->
                                entity(Jackson.unmarshaller(Model.class), model -> {
                                    CompletionStage<Done> futureSaved = test(model);
                                    return onSuccess(futureSaved, done ->
                                            complete("order created")
                                    );
                                })))



                );
//                post(() ->
//                        patch("create-model", () ->
//                                entity(Jackson.unmarshaller(AbstractModel.class), model -> {
//                                    CompletionStage<Done> futureSaved = getInsertMongoDBDoc(model);
//                                    return onSuccess(futureSaved, done ->
//                                            complete("model created")
//                                    );
//                                } ))
//                );
    }

    private CompletionStage<Done> getInsertMongoDBDoc(AbstractModel model) {
        InsertMongoDBDoc doc = new InsertMongoDBDoc();
        Map<String,String> map = new HashMap<>();
        map.put("name", "ling");
        doc.setDocMap(map);
        doc.setConnName("test");
        doc.setCollectionName("test");
        System.out.println("11111");
        mongoDBActorRef.tell(doc);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> test(Model model) {
        InsertMongoDBDoc doc = new InsertMongoDBDoc();
        Map<String,String> map = new HashMap<>();
        map.put("name", "ling");
        doc.setDocMap(map);
        doc.setConnName("test");
        doc.setCollectionName("test");
        System.out.println("11111");
        mongoDBActorRef.tell(doc);
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
