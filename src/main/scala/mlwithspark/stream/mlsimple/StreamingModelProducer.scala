package mlwithspark.stream.mlsimple

import java.net.ServerSocket
import java.io.PrintWriter

import breeze.linalg.DenseVector

import scala.util.Random

/**
  * Created by xiaoqiang on 16-6-5.
  *
  * 基于流计算的机器学习算法实例
  *
  * StreamingModelProducer： 数据生成器
  */
object StreamingModelProducer {
  def main(args: Array[String]) {
    val MaxEvents = 100     // 每秒处理活动最大的数目
    val NumFeatures = 100   //

    val random = new Random()

    // Function to generate a normally distributed dense vector
    // 生成服从正太分布的稠密向量函数
    def generateRandomArray(n: Int) = Array.tabulate(n)(_ => random.nextGaussian())

    // Generate a fixed random model weight vector
    // 生成一个确定的随机模型权重向量
    val w = new DenseVector(generateRandomArray(NumFeatures))
    val intercept = random.nextGaussian() * 10

    // generate a number of random product events
    // 创建一些随机数据事件
    def generateNoisyData(n: Int) = {
      (1 to n).map{i =>
        val x = new DenseVector(generateRandomArray(NumFeatures))
        val y: Double = w.dot(x)      // dot 操作
        val noisy = y + intercept     // noisy 生成方式
        (noisy, x)
      }
    }

    // generate a network producer
    // 创建网络生成器
    val listener = new ServerSocket(9999)
    println("Listening on port: 9999")

    while (true) {
      val socket = listener.accept()
      new Thread() {
        override def run = {
          println("Got client connected from: " + socket.getInetAddress)
          val out = new PrintWriter(socket.getOutputStream(), true)

          while (true) {
            Thread.sleep(1000)
            val num = random.nextInt(MaxEvents)
            val data = generateNoisyData(num)
            data.foreach { case (y, x) =>
              val xStr = x.data.mkString(",")
              val eventStr = s"$y\t$xStr"
              out.write(eventStr)
              out.write("\n")
            }
            out.flush()
            println(s"Created $num events...")
          }
          socket.close()
        }
      }.start()
    }
  }
}
