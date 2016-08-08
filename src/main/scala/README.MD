######################################################################################################
# Caused by: java.io.IOException: com.mysql.jdbc.Driver
######################################################################################################
1. 解决
    下载mysql-connector-java-5.1.39.tar.gz 驱动加入到lib path中
2. 日志记录
16/05/25 09:58:21 ERROR Executor: Exception in task 0.0 in stage 1.0 (TID 1)
java.io.IOException: com.mysql.jdbc.Driver
	at org.apache.hadoop.mapreduce.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:185)
	at org.apache.hadoop.mapred.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:71)
	at org.apache.spark.SparkHadoopWriter.open(SparkHadoopWriter.scala:91)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1193)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1185)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:66)
	at org.apache.spark.scheduler.Task.run(Task.scala:89)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:214)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
	at java.lang.Thread.run(Thread.java:745)
16/05/25 09:58:21 WARN TaskSetManager: Lost task 0.0 in stage 1.0 (TID 1, localhost): java.io.IOException: com.mysql.jdbc.Driver
	at org.apache.hadoop.mapreduce.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:185)
	at org.apache.hadoop.mapred.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:71)
	at org.apache.spark.SparkHadoopWriter.open(SparkHadoopWriter.scala:91)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1193)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1185)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:66)
	at org.apache.spark.scheduler.Task.run(Task.scala:89)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:214)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
	at java.lang.Thread.run(Thread.java:745)

16/05/25 09:58:21 ERROR TaskSetManager: Task 0 in stage 1.0 failed 1 times; aborting job
16/05/25 09:58:21 INFO TaskSchedulerImpl: Removed TaskSet 1.0, whose tasks have all completed, from pool
16/05/25 09:58:21 INFO TaskSchedulerImpl: Cancelling stage 1
16/05/25 09:58:21 INFO DAGScheduler: ResultStage 1 (saveAsHadoopDataset at SimpleDbTest.scala:38) failed in 0.120 s
16/05/25 09:58:21 INFO DAGScheduler: Job 1 failed: saveAsHadoopDataset at SimpleDbTest.scala:38, took 0.165447 s
Exception in thread "main" org.apache.spark.SparkException: Job aborted due to stage failure: Task 0 in stage 1.0 failed 1 times, most recent failure: Lost task 0.0 in stage 1.0 (TID 1, localhost): java.io.IOException: com.mysql.jdbc.Driver
	at org.apache.hadoop.mapreduce.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:185)
	at org.apache.hadoop.mapred.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:71)
	at org.apache.spark.SparkHadoopWriter.open(SparkHadoopWriter.scala:91)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1193)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1185)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:66)
	at org.apache.spark.scheduler.Task.run(Task.scala:89)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:214)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
	at java.lang.Thread.run(Thread.java:745)

Driver stacktrace:
	at org.apache.spark.scheduler.DAGScheduler.org$apache$spark$scheduler$DAGScheduler$$failJobAndIndependentStages(DAGScheduler.scala:1431)
	at org.apache.spark.scheduler.DAGScheduler$$anonfun$abortStage$1.apply(DAGScheduler.scala:1419)
	at org.apache.spark.scheduler.DAGScheduler$$anonfun$abortStage$1.apply(DAGScheduler.scala:1418)
	at scala.collection.mutable.ResizableArray$class.foreach(ResizableArray.scala:59)
	at scala.collection.mutable.ArrayBuffer.foreach(ArrayBuffer.scala:47)
	at org.apache.spark.scheduler.DAGScheduler.abortStage(DAGScheduler.scala:1418)
	at org.apache.spark.scheduler.DAGScheduler$$anonfun$handleTaskSetFailed$1.apply(DAGScheduler.scala:799)
	at org.apache.spark.scheduler.DAGScheduler$$anonfun$handleTaskSetFailed$1.apply(DAGScheduler.scala:799)
	at scala.Option.foreach(Option.scala:236)
	at org.apache.spark.scheduler.DAGScheduler.handleTaskSetFailed(DAGScheduler.scala:799)
	at org.apache.spark.scheduler.DAGSchedulerEventProcessLoop.doOnReceive(DAGScheduler.scala:1640)
	at org.apache.spark.scheduler.DAGSchedulerEventProcessLoop.onReceive(DAGScheduler.scala:1599)
	at org.apache.spark.scheduler.DAGSchedulerEventProcessLoop.onReceive(DAGScheduler.scala:1588)
	at org.apache.spark.util.EventLoop$$anon$1.run(EventLoop.scala:48)
	at org.apache.spark.scheduler.DAGScheduler.runJob(DAGScheduler.scala:620)
	at org.apache.spark.SparkContext.runJob(SparkContext.scala:1832)
	at org.apache.spark.SparkContext.runJob(SparkContext.scala:1845)
	at org.apache.spark.SparkContext.runJob(SparkContext.scala:1922)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1.apply$mcV$sp(PairRDDFunctions.scala:1213)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1.apply(PairRDDFunctions.scala:1156)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1.apply(PairRDDFunctions.scala:1156)
	at org.apache.spark.rdd.RDDOperationScope$.withScope(RDDOperationScope.scala:150)
	at org.apache.spark.rdd.RDDOperationScope$.withScope(RDDOperationScope.scala:111)
	at org.apache.spark.rdd.RDD.withScope(RDD.scala:316)
	at org.apache.spark.rdd.PairRDDFunctions.saveAsHadoopDataset(PairRDDFunctions.scala:1156)
	at basic.db.SimpleDbTest$.operation_db(SimpleDbTest.scala:38)
	at basic.db.SimpleDbTest$.main(SimpleDbTest.scala:17)
	at basic.db.SimpleDbTest.main(SimpleDbTest.scala)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
Caused by: java.io.IOException: com.mysql.jdbc.Driver
	at org.apache.hadoop.mapreduce.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:185)
	at org.apache.hadoop.mapred.lib.db.DBOutputFormat.getRecordWriter(DBOutputFormat.java:71)
	at org.apache.spark.SparkHadoopWriter.open(SparkHadoopWriter.scala:91)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1193)
	at org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1$$anonfun$13.apply(PairRDDFunctions.scala:1185)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:66)
	at org.apache.spark.scheduler.Task.run(Task.scala:89)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:214)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
	at java.lang.Thread.run(Thread.java:745)



序列化类的写法
Kryo在序列化的时候缓存空间默认大小是2MB，可以更具具体的业务模型调整该大小，具体方式
Spark.kryoserializer.buffer 设置为10MB等。
conf.registryKryoClass(Array(classOf[Person]));

#######################################################################################
scala中的类
#######################################################################################
1. scala中对setter，getter的规则
（1）. class MongoClient(var host:String, var port:Int)
使用var声明field,则该field将同时拥有getter和setter

（2）. class MongoClient(val host:String, val port:Int)
使用val声明field,则该field只有getter。这意味着在初始化之后，你将无法再修改它的值。

（3）.class MongoClient(host:String, port:Int)
如果没有任何修饰符，则该field是完全私有的。


2. 显示定义一个字段的getter和setter方法
（1）字段名应以_在前缀，如_age
（2）getter是一个function,其命名在字段名上去除_即可，如def age=_age
（3）setter的定义看似有些奇怪，其实只是一些约定，熟悉以后就可以了。
setter的命名是在去除字段名上去除前缀，然后在后面添加”=”后缀，对是”_=”！
然后再接参数列表，再之后就和普通的函数定义没有区别了！

例如：
class Person(var firstName:String, var lastName:String, private var _age:Int) {
   def age = _age //age是一个function,因为没有参数，所以参数列表为空，它是为私有字段_age定义的getter
   def age_=(newAge: Int) = _age = newAge //“age_=”是setter的函数名！这里如果写成def age_=(newAge: Int) = ｛_age = newAge｝看上去就不奇怪了。
}
之后，我们可以这样使用：

val p = new Person("Nima", "Raychaudhuri", 2)
p.age = 3

3. 但是注意对于Case Class，则稍有不同！
Case Class对于通过Primary Constructor声明的字段自动添加val修饰，使之变为只读的。
scala> case class Person2(firstName:String)
defined class Person2

scala> val p=Person2("Jim")
p: Person2 = Person2(Jim)

scala> p.firstName
res5: String = Jim

scala> p.firstName="Tom"
<console>:10: error: reassignment to val
       p.firstName="Tom"


4. 对于重载构造函数：它的第一个语句必须是调用另外一个重载的构造函数或者是主构造函数！
当然除了主构造函数以外！这个表述如果再深入地一想，那么我们就可以想到：
所有的构造函数在一开始就会首先调用主函数！！这也是为什么：scala对写在Class内的零星的脚本和代码片段
的处理是通过移到主构造函数内去执行的原因！

例如：
   class MongoClient(val host:String, val port:Int) {
       def this() = {
           val defaultHost = "127.0.0.1"
           val defaultPort = 27017
           this(defaultHost, defaultPort)
       }
   }
   在上面的代码汇编出错，因为它的第一行并不是在调用另外一个构造函数！


#######################################################################################
scala中的Object(类似于java中static)
#######################################################################################

1. 基于“纯OO”的理念，scala里没有static关键字的：一切皆对象!
但是为了能实现“全局唯一”的需要，scala支持一种叫做singleton object的对象，也就是限定一个
类的实例只有唯一一个的机制。

object RichConsole {
    def p(x: Any) = println(x)
}

scala> RichConsole.p("rich console")
rich console
上述代码定义了一个object：RichConsole，对于它的调用和与java里调用static的字段和方法是极其类似的！

2. 像使用function一样使用object.
实际上，这是一种“约定”，只要你的object中定义了一个apply方法，你是可以直接使用object的名字作为函数名后接与
 apply方法对应的参数就可以实现对这个apply方法的调用！我们看下面的这个例子：

scala> object A {
     | def apply(s:String)=println(s)
     | }
defined object A

scala> A("SDFSDF")
SDFSDF




