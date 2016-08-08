package basic.func

/**
  * Created by xiaoqiang on 16-5-21.
  * Scala的解释器在解析函数参数(function arguments)时有两种方式：
传值调用（call-by-value）：先计算参数表达式的值，再应用到函数内部；
传名调用（call-by-name）：将未计算的参数表达式直接应用到函数内部
在进入函数内部前，传值调用方式就已经将参数表达式的值计算完毕，而传名调用是在函数内部进行参数表达式的值计算的。
  */
object FuncCallByName {
  def main(args: Array[String]) {
    delayed(time())

    println ("======================")
    noDelayed(time())
  }

  def time() = {
    println("Getting time in nano seconds")
    System.nanoTime()
  }

  def delayed( t: => Long) = {      // 传名调用
    println("In Delayed Method")
    println("Param: " + t)
    t
  }

  def noDelayed( t: Long) = {     // 传值调用
    println("In NoDelayed Method")
    println("Param: " + t)
    println ("t is: " + t)
  }
}


/**
  *Result:
In Delayed Method
Getting time in nano seconds
Param: 44424347472367
Getting time in nano seconds
======================
Getting time in nano seconds
In NoDelayed Method
Param: 44424347604976
t is: 44424347604976
  **/