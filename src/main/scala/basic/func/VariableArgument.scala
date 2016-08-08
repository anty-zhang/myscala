package basic.func

/**
  * Created by xiaoqiang on 16-5-21.
  * 函数中传入的变量个数可变的
  */
object VariableArgument {
  def main(args: Array[String]) {
    printStrings("Hello", "Scala", "Python")
  }

  def printStrings( args:String* ) = {
    var i : Int = 0
    for( arg <- args ){
      println("Arg value[" + i + "] = " + arg )
      i = i + 1
    }
  }
}
