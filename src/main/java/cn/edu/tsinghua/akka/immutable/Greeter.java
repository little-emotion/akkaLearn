package cn.edu.tsinghua.akka.immutable;

import akka.actor.UntypedAbstractActor;
import com.alibaba.fastjson.JSONObject;

public class Greeter extends UntypedAbstractActor {

    @Override
    public void onReceive(Object msg) throws InterruptedException {

        if(msg instanceof Message){
            System.out.println("Greeter收到的数据为：" + JSONObject.toJSONString(msg));
            getSender().tell("Greeter工作完成。", getSelf());//给发送者发送信息.
        }else{
            unhandled(msg);
        }

    }

}