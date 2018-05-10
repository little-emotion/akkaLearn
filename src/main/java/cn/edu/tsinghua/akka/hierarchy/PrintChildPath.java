package cn.edu.tsinghua.akka.hierarchy;

import akka.actor.*;
import cn.edu.tsinghua.akka.MyWork;

public class PrintChildPath extends UntypedAbstractActor {

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message.equals("printit")) {
            ActorRef secondRef = getContext().actorOf(MyWork.props(), "second-actor");
            System.out.println("Second: " + secondRef);
        } else {
            unhandled(message);
        }
    }

    public static void main(String[] args) throws java.io.IOException {
        ActorSystem system = ActorSystem.create("testSystem");

        ActorRef firstRef = system.actorOf(Props.create(PrintChildPath.class), "first-actor");
        System.out.println("First: " + firstRef);
        firstRef.tell("printit", ActorRef.noSender());

        system.terminate();
    }

}
