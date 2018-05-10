package cn.edu.tsinghua.akka.helloword;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Printer初始化一个空的
 *
 * 接收到 Greeting 后，执行log打印动作
 */
public class Printer extends AbstractActor {

    private String name;
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(String name) {
        return Props.create(Printer.class, () -> new Printer(name));
    }

    //#printer-messages
    static public class Greeting {
        public final String message;
        public Greeting(String message) {
            this.message = message;
        }
    }



    public Printer(String name) {
        this.name = name;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Greeting.class, greeting -> {
                    log.info(name + ": "+ greeting.message);
                    getSender().tell(Greeter.Msg.DOWN, getSelf());
                })
                .build();
    }
}