package things.brain;

import akka.Done;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

import java.util.concurrent.CompletionStage;
import akka.http.javadsl.marshallers.jackson.Jackson;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/19 11:22
 * @description：http server
 */
public class HttpServer extends AllDirectives {
    public Route createRoute() {
        return concat(
                path("hello", () ->
                        get(() ->
                                complete("<h1>Say hello to akka-http</h1>")))

//                post(() ->
//                        path("create-order", () ->
//                                entity(Jackson.unmarshaller(Order.class), order -> {
//                                    CompletionStage<Done> futureSaved = saveOrder(order);
//                                    return onSuccess(futureSaved, done ->
//                                            complete("order created")
//                                    );
//                                })))
//
//
//
                );
    }
}
