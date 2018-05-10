package cn.edu.tsinghua.akka.failurehandle;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import cn.edu.tsinghua.akka.MyWork;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


/**
 * SuperVisor actor生成RestartActor，并接收RestartActor抛出的异常，并对RestartActor进行处理
 */
public class OverrideHandler extends UntypedAbstractActor{

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    ActorRef child = getContext().actorOf(MyWork.props(), "restartActor");

    /**
     * 配置自己的strategy
     * @return
     */
    @Override
    public SupervisorStrategy supervisorStrategy(){
        return new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES),//一分钟内重试3次，超过则kill掉actor
                new Function<Throwable, SupervisorStrategy.Directive>() {
                    @Override
                    public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                        if(throwable instanceof ArithmeticException){//ArithmeticException是出现异常的运算条件时，抛出此异常。例如，一个整数“除以零”时，抛出此类的一个实例。
                            log.info("meet ArithmeticException ,just resume.");
                            return  SupervisorStrategy.resume();//继续; 重新开始; 恢复职位;
                        }else if(throwable instanceof NullPointerException){
                            log.info("meet NullPointerException , restart.");
                            return SupervisorStrategy.restart();
                        }else if(throwable instanceof IllegalArgumentException){
                            log.info("meet IllegalArgumentException ,stop.");
                            return SupervisorStrategy.stop();
                        }else{
                            log.info("escalate.");
                            return SupervisorStrategy.escalate();//使逐步升级; 使逐步上升; 乘自动梯上升;也就是交给更上层的actor处理。抛出异常
                        }
                    }
                });
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        unhandled(o);
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("strategy", ConfigFactory.load("akka.config"));
        ActorRef superVisor = system.actorOf(Props.create(OverrideHandler.class), "SuperVisor");

        ActorSelection actorSelection = system.actorSelection("akka://strategy/user/SuperVisor/restartActor");//这是akka的路径。restartActor是在SuperVisor中创建的。
        actorSelection.tell(MyWork.Msg.RESTART, ActorRef.noSender());
        actorSelection.tell(MyWork.Msg.RESTART, ActorRef.noSender());
        actorSelection.tell(MyWork.Msg.RESTART, ActorRef.noSender());
        actorSelection.tell(MyWork.Msg.RESUME, ActorRef.noSender());
        actorSelection.tell(MyWork.Msg.RESUME, ActorRef.noSender());
        actorSelection.tell(MyWork.Msg.RESTART, ActorRef.noSender());  //第四次RESTART导致supervisor关掉了RestartActor
        actorSelection.tell(MyWork.Msg.RESUME, ActorRef.noSender());
        actorSelection.tell(MyWork.Msg.RESTART, ActorRef.noSender());

    }

}