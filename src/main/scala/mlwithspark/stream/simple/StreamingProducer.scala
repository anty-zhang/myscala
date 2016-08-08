package mlwithspark.stream.simple

import java.io.PrintWriter
import java.net.ServerSocket

import scala.util.Random
/**
  * Created by xiaoqiang on 16-6-4.
  *
  * Spark Stream 实例
  * 为一个随机的用户随机生成一个产品，并通过socket发送出去
  */
object StreamingProducer {
  def main(args: Array[String]) {
    val random = new Random()

    // Maximum number of events per second
    // 每秒最大活动数
    val MaxEvents = 6

    // 从文件name.csv中读取名字列表
    val nameSource = this.getClass.getResourceAsStream("/name.csv")
    val names = scala.io.Source.fromInputStream(nameSource)
                  .getLines()
                  .toList
                  .head
                  .split(",")   // [Ljava.lang.String;@67df3314
                  .toSeq    // WrappedArray(Miguel, Eric, James, Juan, Shawn, James, Doug, Gary, Frank, Janet, Michael, James, Malinda, Mike, Elaine, Kevin, Janet, Richard, Saul, Manuela)

    println("names: " + names)


    // generate a sequence of possible products
    val products = Seq(
      "iPhone cover" -> 9.99,
      "Headphones" -> 5.49,
      "Samsung Galaxy Cover" -> 8.67,
      "iPad Cover" -> 7.49
    )
    println("products: " + products)

    // generate a number of random product event
    // 两种随机方式的写法
    def generateProductEvent(n: Int) = {
      (1 to n).map{i =>
        val (product, price) = products(random.nextInt(products.size))
        val user = random.shuffle(names).head

        (user, product, price)
      }
    }

    // create a network producer
    println("Listening on port: 9999")
    val listener = new ServerSocket(9999)
    while (true) {
      val socket = listener.accept()

      new Thread() {
        override def run = {
          println("Got client connected from " + socket.getInetAddress)
          val out = new PrintWriter(socket.getOutputStream(), true)

          while (true) {
            Thread.sleep(1000)
            val num = random.nextInt(MaxEvents)
            val productEvent = generateProductEvent(num)    // 获取num个随机的用户购买记录

            // 写数据
            productEvent.foreach{event =>
              out.write(event.productIterator.mkString(","))
              out.write("\n")
            }
            out.flush()
            println(s"Create $num events...")
          }
          socket.close()    // 关闭socket
        }
      }.start()

    }
  }
}
