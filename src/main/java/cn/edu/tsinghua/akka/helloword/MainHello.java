package cn.edu.tsinghua.akka.helloword;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;


/**
 * 通过各个 Actor 的 props 函数构造对应的 Actor，并赋予名字
 */
public class MainHello {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("MySystem");
        final ActorRef printer =
                system.actorOf(Printer.props("main-printer"), "main-printer");

        System.out.println(printer.path());
        final ActorRef greeter =
                system.actorOf(Greeter.props("Greeter", printer), "greeter");

        //#main-send-messages
        greeter.tell(new Greeter.WhoToGreet("Akka"), ActorRef.noSender());
        greeter.tell(new Greeter.Greet(), ActorRef.noSender());

        system.terminate();
    }
}
