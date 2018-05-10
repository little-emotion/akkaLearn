package cn.edu.tsinghua.akka.failurehandle;

import akka.actor.*;
import cn.edu.tsinghua.akka.MyWork;
import com.typesafe.config.ConfigFactory;

/**
 * 默认对异常的处理为 stop 并 restart 挂掉的actor
 */
public class DefaultHandler extends AbstractActor {
    ActorRef child = getContext().actorOf(Props.create(MyWork.class), "supervised-actor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("failChild", f -> {
                    child.tell(MyWork.Msg.EXCEPTION, getSelf());
                })
                .matchEquals("work", f -> {
                    child.tell(MyWork.Msg.WORKING, getSelf());
                })
                .build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("strategy", ConfigFactory.load("akka.config"));
        ActorRef supervisingActor = system.actorOf(Props.create(DefaultHandler.class), "supervising-actor");
        supervisingActor.tell("failChild", ActorRef.noSender());
        supervisingActor.tell("work", ActorRef.noSender());
    }

}
