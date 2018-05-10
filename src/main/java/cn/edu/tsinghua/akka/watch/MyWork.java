package cn.edu.tsinghua.akka.watch;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * UntypedAbstractActor是AbstractActor的子类
 */
public class MyWork extends UntypedAbstractActor {

    LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    public static enum Msg {
        WORKING, DONE, CLOSE
    }

    @Override
    public void preStart() {
        logger.info("preStart!");
    }

    @Override
    public void postStop() throws Exception {
        logger.info("postStop!");
    }

    @Override
    public void onReceive(Object msg) {
        try {
            if (msg == Msg.WORKING) {
                logger.info("I am  working");
            } else if (msg == Msg.DONE) {
                logger.info("I am down");
            } else if (msg == Msg.CLOSE) {
                logger.info("I am close");
                getSender().tell(Msg.CLOSE, getSelf());
                getContext().stop(getSelf());
            } else {
                unhandled(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
