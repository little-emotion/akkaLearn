package cn.edu.tsinghua.akka.supervisor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by liubenlong on 2017/1/9.
 * 监督者，监督策略
 */
public class SuperVisor extends UntypedAbstractActor{

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


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
        if(o instanceof Props){
            getContext().actorOf((Props)o , "restartActor");
        }else{
            unhandled(o);
        }
    }
}