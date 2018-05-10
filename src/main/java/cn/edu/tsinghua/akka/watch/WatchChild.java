package cn.edu.tsinghua.akka.watch;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import cn.edu.tsinghua.akka.MyWork;
import com.typesafe.config.ConfigFactory;

public class WatchChild extends UntypedAbstractActor {

    LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
    ActorRef child = getContext().actorOf(Props.create(MyWork.class), "MyWork");

    static public Props props() {
        return Props.create(WatchChild.class, () -> new WatchChild());
    }

    /**
     * 监听一个actor，这个actor挂了，就会给WatchActor发送个Terminated信号
     */
    public WatchChild() {
        getContext().watch(child);
    }

    @Override
    public void onReceive(Object msg) throws InterruptedException {
        if (msg instanceof Terminated) {
            //这里简单打印一下，然后停止system
            logger.error(((Terminated) msg).getActor().path() + " has Terminated. now shutdown the system");
            getContext().system().terminate();
        } else if (msg.equals("stopChild")) {
            child.tell(PoisonPill.getInstance(), getSelf());
//            child.tell(MyWork.Msg.STOP, getSelf());
        } else if (msg.equals("work")) {
            child.tell(MyWork.Msg.WORKING, getSelf());

        } else {
            unhandled(msg);
        }

    }

    public static void main(String[] args) {
        //创建ActorSystem。一般来说，一个系统只需要一个ActorSystem。
        //参数1：系统名称。参数2：配置文件
        ActorSystem system = ActorSystem.create("Hello", ConfigFactory.load("akka.config"));

        ActorRef watchActor = system.actorOf(WatchChild.props(), "SuperVisor");
        watchActor.tell("work", ActorRef.noSender());
        watchActor.tell("stopChild", ActorRef.noSender());
        watchActor.tell("work", ActorRef.noSender());

    }

}