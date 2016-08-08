package basic.func

import java.util.Date
/**
  * Created by xiaoqiang on 16-5-21.
  * 偏应用函数
  */
object PartiallyApplied {
  def main(args: Array[String]) {
    // 正常的逻辑写法
    val date = new Date
    log(date, "message1")
    log(date, "message2")
    log(date, "message3")

    println("===================================")

    // 偏函数的写法
    val logWithDateBound = log(date, _: String)
    logWithDateBound("m1")
    logWithDateBound("m2")
    logWithDateBound("m3")
  }


  def log(date: Date, message: String) = {
    println(date + "----" + message)
  }


}


/**
Sat May 21 11:15:10 CST 2016----message1
Sat May 21 11:15:10 CST 2016----message2
Sat May 21 11:15:10 CST 2016----message3
===================================
Sat May 21 11:15:10 CST 2016----m1
Sat May 21 11:15:10 CST 2016----m2
Sat May 21 11:15:10 CST 2016----m3
**/