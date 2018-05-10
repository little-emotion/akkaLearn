package cn.edu.tsinghua.akka.future;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import cn.edu.tsinghua.akka.MyWork;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by liubenlong on 2017/1/12.
 */
public class PrintActor extends UntypedAbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object o) throws Throwable {
        log.info("akka.future.PrintActor.onReceive:" + o);
        unhandled(o);
    }

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("strategy", ConfigFactory.load("akka.config"));
        ActorRef printActor = system.actorOf(Props.create(PrintActor.class), "PrintActor");
        ActorRef workerActor = system.actorOf(Props.create(MyWork.class), "WorkerActor");

        //等等future返回，设置超时时间为4000ms
        Future<Object> future = Patterns.ask(workerActor, MyWork.Msg.SLEEP, 4000);
        System.out.println("waiting");
        String result = (String) Await.result(future, Duration.create(4, TimeUnit.SECONDS));
        System.out.println("result:" + result);

        //不等待返回值，有返回值来的时候将会重定向到printActor
        Future<Object> future1 = Patterns.ask(workerActor, MyWork.Msg.SLEEP, 4000);
        Patterns.pipe(future1, system.dispatcher()).to(printActor);
        System.out.println("do not wait");

        //kill MyWork
        workerActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}