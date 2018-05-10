package cn.edu.tsinghua.akka.inbox;

import akka.actor.*;
import cn.edu.tsinghua.akka.MyWork;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by liubenlong on 2017/1/12.
 */
public class InboxTest {

    public static void main(String [] args){
        ActorSystem system = ActorSystem.create("inbox", ConfigFactory.load("akka.conf"));

        ActorRef inboxTest1 = system.actorOf(Props.create(MyWork.class), "InboxTest1");

        Inbox inbox = Inbox.create(system);
        inbox.watch(inboxTest1);//监听一个actor

        //通过inbox来发送消息
        inbox.send(inboxTest1, MyWork.Msg.WORKING);
        inbox.send(inboxTest1, MyWork.Msg.STOP);


        while(true){
            try {
                Object receive = inbox.receive(Duration.create(1, TimeUnit.SECONDS));
                if(receive == MyWork.Msg.STOP){//收到的inbox的消息
                    System.out.println("inboxTextActor is closing");
                }else if(receive instanceof  Terminated){//中断 ，和线程一个概念
                    System.out.println(((Terminated) receive).getActor().path() + " is closed");
                    system.terminate();
                    break;
                }else {
                    System.out.println(receive);
                }
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}