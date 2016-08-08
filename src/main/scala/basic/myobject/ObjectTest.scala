package basic.myobject


/**
  * Created by xiaoqiang on 16-6-11.
  * 这是scala里一个典型简单工厂（静态工厂方法)模式的实现:
  */


abstract class Role { def canAccess(page: String): Boolean}

class Root extends Role{
  override def canAccess(page: String) = true
}

class SuperAnalyst extends Role {
  override def canAccess(page: String) = page != "Admin"
}

class Analyst extends Role {
  override def canAccess(page: String) = false
}


//下面的object Role或许命名为RoleFactory更为恰当
//但是考虑到后面在使用它创建对象时 val root = Role("root") 的写法，
// 还是定义为Role为好！

object Role {
  def apply(roleName: String) = roleName match {
    case "root" => new Root
    case "superAnalyst" => new SuperAnalyst
    case "analyst" => new Analyst
  }
}


object ObjectTest {
  def main(args: Array[String]) {
    //创建对象：
    val root = Role("root")  //这里等同于Role.apply("root")
    val analyst = Role("analyst")
  }
}
