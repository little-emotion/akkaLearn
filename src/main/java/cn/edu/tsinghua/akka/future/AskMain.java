package cn.edu.tsinghua.akka.future;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by liubenlong on 2017/1/16.
 */
public class AskMain {

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("strategy", ConfigFactory.load("akka.config"));
        ActorRef printActor = system.actorOf(Props.create(PrintActor.class), "PrintActor");
        ActorRef workerActor = system.actorOf(Props.create(WorkerActor.class), "WorkerActor");

        //等等future返回
        Future<Object> future = Patterns.ask(workerActor, 5, 4000);
        System.out.println("waiting");
        int result = (int) Await.result(future, Duration.create(4, TimeUnit.SECONDS));
        System.out.println("result:" + result);

        //不等待返回值，直接重定向到其他actor，有返回值来的时候将会重定向到printActor
        Future<Object> future1 = Patterns.ask(workerActor, 8, 4000);
        Patterns.pipe(future1, system.dispatcher()).to(printActor);
        System.out.println("do not wait");

        workerActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}
