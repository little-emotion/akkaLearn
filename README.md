
## Actor模型

Carl Hewitt在1973年提出了Actor模型。

Actor模型是一个并发编程的数学模型，把“actors”作为并发计算的通用原语。当接收到一个消息后，actor可以做出以下反应：做出本地决策，创造更多actor，发送更多消息，并且决定如何对下一条消息做出反应。Actor可以改变自己的状态，但是只能通过消息传递来影响其他actor（避免了锁的使用）。


## Akka

https://doc.akka.io/docs/akka/current/guide/introduction.html

Akka允许你关注业务需求而不需要写底层代码来提供可靠性、容错和高性能。

* 可以避免使用原子操作或锁机制实现多线程，将你从内存可见性问题中解放出来（内存可见性是指一旦线程修改数据对象，其它线程在修改行为发生之后马上能看见此对象的新状态）

* 系统与组件间透明的远程交流：将你从复杂的网络编程中解放出来

* 一个分布式的、高可用的、可随意伸缩的架构：使你能够实现真正的响应式系统

## 举例时间

Actor模型类似等级制度，上级对下级拥有绝对生杀大权。每个人都在自己的房间活动，只能通过发邮件与其他人联系，每个人可以根据邮件做出反应。任何异常都需要向上级发邮件通知。


由于这个模型很贴近人类社会，所以程序员可以把精力放在如何对应到业务逻辑上。


## Actor生命周期

preStart() is invoked after the actor has started but before it processes its first message.
postStop() is invoked just before the actor stops. No messages are processed after this point.



* 启动一个actor并restart的调用流程：原实例为actor1，新实例为actor2
	* actor1.preStart()
	* actor1.preRestart()
	* actor2.postRestart(){默认postRestart会调用preStart方法}
	* actor2.postStop()

* 启动一个actor并resume其的调用流程：原实例为actor1
	* actor1.preStart()

* 启动一个actor并stop其的调用流程：原实例为actor1
	* actor1.preStart()
	* actor1.postStop()


