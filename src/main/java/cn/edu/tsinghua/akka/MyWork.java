package cn.edu.tsinghua.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.Option;

import java.io.IOException;

public class MyWork extends UntypedAbstractActor {

    LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    static public Props props() {
        return Props.create(MyWork.class, () -> new MyWork());
    }


    public static enum Msg {
        WORKING, EXCEPTION, STOP, RESTART, RESUME, BACK, SLEEP
    }

    @Override
    public void preStart() {
        System.out.println("MyWork preStart hashCode=" + this.hashCode());
    }

    @Override
    public void postStop() {
        System.out.println("MyWork stopped hashCode=" + this.hashCode());
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        System.out.println("MyWork preRestart hashCode=" + this.hashCode());
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        System.out.println("MyWork postRestart hashCode=" + this.hashCode());
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg == Msg.WORKING) {
            logger.info("I am  working");
        } else if (msg == Msg.EXCEPTION) {
            throw new Exception("I failed!");

        } else if(msg == Msg.RESTART){
            logger.info("I will be restarted");
            throw new NullPointerException();

        } else if (msg == Msg.RESUME) {
            logger.info("I will be resume");
            throw new ArithmeticException();

        }else if (msg == Msg.STOP) {
            logger.info("I am stopped");
            getContext().stop(getSelf());

        }else if (msg == Msg.BACK) {
            getSender().tell("I am alive", getSelf());

        } else if(msg == Msg.SLEEP) {
            logger.info("I am going to sleep");
            Thread.sleep(3000);
            getSender().tell("I am awake", getSelf());

        } else {
            unhandled(msg);
        }
    }

    public static void main(String[] args) throws IOException {
        //初始化 ActorSystem
        final ActorSystem system = ActorSystem.create("MySystem");

        //创建第一个 Actor
        final ActorRef firstActor =
                system.actorOf(MyWork.props(), "firstActor");

        System.out.println(firstActor.path());

        //向第一个 Actor 发送消息
        firstActor.tell(Msg.WORKING, ActorRef.noSender());

        System.out.println(">>> Press ENTER to exit <<<");
        try {
            System.in.read();
        } finally {
            system.terminate();
        }
    }

}
