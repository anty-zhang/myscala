package basic.func

/**
  * Created by xiaoqiang on 16-5-21.
  * 柯里化(Currying)指的是将原来接受两个参数的函数变成新的接受一个参数的函数的过程
  * 新的函数返回一个以原有第二个参数为参数的函数
  *
  * 实例：
  * add(1)(2) 实际上是依次调用两个普通函数（非柯里化函数），
  * 第一次调用使用一个参数 x，返回一个函数类型的值，第二次使用参数y调用这个函数类型的值。
实质上最先演变成这样一个方法：
def add(x:Int)=(y:Int)=>x+y
那么这个函数是什么意思呢？ 接收一个x为参数，返回一个匿名函数，该匿名函数的定义是：接收一个Int型参数y，函数体为x+y。现在我们来对这个方法进行调用。
val result = add(1)
返回一个result，那result的值应该是一个匿名函数：(y:Int)=>1+y
所以为了得到结果，我们继续调用result。
val sum = result(2)
最后打印出来的结果就是3。
  */
object CurryingFunc {
  def main(args: Array[String]) {
    val str1 = "Hello"
    val str2 = "Scala"
    println(concat1(str1)(str2))
    println(concat2(str1)(str2))
  }

  // first method
  def concat1(s1: String)(s2: String) = {
    s1 + s2
  }

  // second method
  def concat2(s1: String) = (s2: String) =>  s1 + " " + s2
}
