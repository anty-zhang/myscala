package basic.oop

/**
  * Created by xiaoqiang on 16-8-9.
  */
object TestPrivateParams {
  def main(args: Array[String]) {
    val p = new Point(1, 2)
    p.move(1, 1)
  }
}


class Point(val xc: Int, val yc: Int) {
  val x: Int = xc
  val y: Int = yc
  val z: Int = 20

  def move(dx: Int, dy: Int): Unit = {
    val mx = x + dx
    val my = y + dy

    println ("x 的坐标点: " + mx)
    println ("y 的坐标点: " + my)
    println ("z 的坐标点: " + z)
  }


}


