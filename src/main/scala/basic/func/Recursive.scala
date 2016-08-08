package basic.func

/**
  * Created by xiaoqiang on 16-5-21.
  * scala 中递归函数
  */
object Recursive {
  def main(args: Array[String]) {
    for (i <- 1 to 10) {
      println("Factorial of " + i + ": " + factorial(i))
    }
  }

  def factorial(n: BigInt): BigInt = {
    if (n <= 1) {
      1
    } else {
      n * factorial(n - 1)
    }
  }
}
