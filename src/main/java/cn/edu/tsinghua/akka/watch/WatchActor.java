package cn.edu.tsinghua.akka.watch;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class WatchActor extends UntypedAbstractActor {

    LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    /**
     * 监听一个actor，这个actor挂了，就会给WatchActor发送个Terminated信号
     * @param actorRef
     */
    public WatchActor(ActorRef actorRef){
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

}
