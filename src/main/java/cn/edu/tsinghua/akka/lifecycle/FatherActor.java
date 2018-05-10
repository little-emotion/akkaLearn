package cn.edu.tsinghua.akka.lifecycle;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import cn.edu.tsinghua.akka.MyWork;

/**
 * 当前actor停止前，会先stop掉所有子actor。子actor的postStop()会在当前actor的postStop()之前被调用
 */
public class FatherActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


    @Override
    public void preStart() {
        System.out.println("first started");
        getContext().actorOf(Props.create(MyWork.class), "second");
    }

    @Override
    public void postStop() {
        System.out.println("first stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("stop", s -> {
                    getContext().stop(getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("testSystem");
        ActorRef first = system.actorOf(Props.create(FatherActor.class), "first");
        first.tell("stop", ActorRef.noSender());
        system.terminate();
    }
}