package cn.edu.tsinghua.akka;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.Option;

public class MyWork extends UntypedAbstractActor {

    private int i = 0;

    LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    static public Props props() {
        return Props.create(MyWork.class, () -> new MyWork());
    }


    public static enum Msg {
        WORKING, EXCEPTION, STOP, RESTART, RESUME, BACK, SLEEP
    }

    @Override
    public void preStart() {
        System.out.println(i++ +" preStart hashCode=" + this.hashCode());
    }

    @Override
    public void postStop() {
        System.out.println(i++ +" stopped hashCode=" + this.hashCode());
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        System.out.println(i++ +" preRestart hashCode=" + this.hashCode());
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        System.out.println(i++ +" postRestart hashCode=" + this.hashCode());
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg == Msg.WORKING) {
            logger.info("I am  working");
        } else if (msg == Msg.EXCEPTION) {
            throw new Exception("I failed!");

        } else if(msg == Msg.RESTART){
            logger.info("I will be restarted");
            throw new NullPointerException();

        } else if (msg == Msg.RESUME) {
            logger.info("I will be resume");
            throw new ArithmeticException();

        }else if (msg == Msg.STOP) {
            logger.info("I am stopped");
            getContext().stop(getSelf());

        }else if (msg == Msg.BACK) {
            getSender().tell("I am alive", getSelf());

        } else if(msg == Msg.SLEEP) {
            logger.info("I am going to sleep");
            Thread.sleep(3000);
            getSender().tell("I am awake", getSelf());

        } else {
            unhandled(msg);
        }
    }
}
