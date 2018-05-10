package cn.edu.tsinghua.akka.watch;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import cn.edu.tsinghua.akka.MyWork;
import com.typesafe.config.ConfigFactory;

/**
 * Watch Actor观察myWork actor，当myWork actor挂掉后就停止系统
 */
public class WatchAnother extends UntypedAbstractActor {

    LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    static public Props props(ActorRef watchedActor) {
        return Props.create(WatchAnother.class, () -> new WatchAnother(watchedActor));
    }

    /**
     * 监听一个actor，这个actor挂了，就会给WatchActor发送个Terminated信号
     * @param actorRef
     */
    public WatchAnother(ActorRef actorRef){
        getContext().watch(actorRef);
    }

    @Override
    public void onReceive(Object msg) throws InterruptedException {
        if(msg instanceof Terminated){
            //这里简单打印一下，然后停止system
            logger.error(((Terminated)msg).getActor().path() + " has Terminated. now shutdown the system");
            getContext().system().terminate();
        }else{
            unhandled(msg);
        }

    }

    public static void main(String[] args) {
        //创建ActorSystem。一般来说，一个系统只需要一个ActorSystem。
        //参数1：系统名称。参数2：配置文件
        ActorSystem system = ActorSystem.create("Hello", ConfigFactory.load("akka.config"));
        ActorRef myWork = system.actorOf(Props.create(MyWork.class), "MyWork");

        //监听myWork
        ActorRef watchActor = system.actorOf(WatchAnother.props(myWork), "SuperVisor");

        myWork.tell(MyWork.Msg.WORKING, ActorRef.noSender());

        //中断myWork
        myWork.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

}



