package cn.edu.tsinghua.akka.helloword;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Greeter初始化包括一个自己独有的message和对应的printer actor
 *
 * 接收到 WhoToGreat 后，就执行message拼接动作
 * 接收到 Greet 后，就向printer发送 Greeting
 */
public class Greeter extends AbstractActor {

    private final String name;
    private final ActorRef printerActor;
    private String greeting = "";
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public Greeter(String message, ActorRef printerActor) {
        this.name = message;
        this.printerActor = printerActor;
    }


    static public Props props(String message, ActorRef printerActor) {
        return Props.create(Greeter.class, () -> new Greeter(message, printerActor));
    }


    //#greeter-messages
    static public class WhoToGreet {
        public final String who;

        public WhoToGreet(String who) {
            this.who = who;
        }
    }


    //#greeter-messages
    static public class Greet {
        public Greet() {
        }
    }

    public static enum Msg {
        DOWN
    }

    @Override
    public void preStart() {
        log.info(name + ": preStart");
    }

    public void postStop() {
        log.info(name + ": postStop");
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WhoToGreet.class, wtg -> {
                    this.greeting = name + ": " + wtg.who;
                })
                .match(Greet.class, x -> {
                    //#greeter-send-name
                    printerActor.tell(new Printer.Greeting(greeting), getSelf());
                })
                .matchEquals(Msg.DOWN, o -> {
                    log.info("I am down");
                    getContext().stop(getSelf());
                    getContext().system().terminate();
                })
                .build();
    }
}
