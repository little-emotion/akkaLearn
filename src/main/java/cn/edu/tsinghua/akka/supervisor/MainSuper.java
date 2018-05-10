package cn.edu.tsinghua.akka.supervisor;

import akka.actor.*;
import com.typesafe.config.ConfigFactory;

/**
 * SuperVisor actor生成RestartActor，并接收RestartActor抛出的异常，并对RestartActor进行处理
 */
public class MainSuper {


    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("strategy", ConfigFactory.load("akka.config"));
        ActorRef superVisor = system.actorOf(Props.create(SuperVisor.class), "SuperVisor");
        superVisor.tell(RestartActor.props(), ActorRef.noSender());

        ActorSelection actorSelection = system.actorSelection("akka://strategy/user/SuperVisor/restartActor");//这是akka的路径。restartActor是在SuperVisor中创建的。
        actorSelection.tell(RestartActor.Msg.RESTART, ActorRef.noSender());
        actorSelection.tell(RestartActor.Msg.RESTART, ActorRef.noSender());
        actorSelection.tell(RestartActor.Msg.RESTART, ActorRef.noSender());
        actorSelection.tell(RestartActor.Msg.RESUME, ActorRef.noSender());
        actorSelection.tell(RestartActor.Msg.RESUME, ActorRef.noSender());
        actorSelection.tell(RestartActor.Msg.RESTART, ActorRef.noSender());  //第四次RESTART导致supervisor关掉了RestartActor
        actorSelection.tell(RestartActor.Msg.RESUME, ActorRef.noSender());
        actorSelection.tell(RestartActor.Msg.RESTART, ActorRef.noSender());
        system.terminate();

    }

}
