package basic.func

/**
  * Created by xiaoqiang on 16-5-21.
  * 高阶函数，调用函数的函数
  */
object HigherOrder {
  def main(args: Array[String]) {
    println (apply(layout, 10))
  }

  def apply(f:Int => String, v: Int) = f(v)


  def layout[A] (x: A) = {
    "[" + x.toString + "]"
  }
}
