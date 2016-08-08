package basic.io

/**
  * Created by xiaoqiang on 16-5-25.
  * scala 文件读写
  */
import java.io.File

import scala.io.Source

object FileReadWrite {
  def main(args: Array[String]) {
//    read()
//    cacheRead()
//    readFromFileToWordAndNumber()
    val dirs = subdirs(new File("/tmp"))
    for (d <- dirs)
      println(d)

  }

  def read(): Unit = {
    // The first argument can be a string or a java.io.File
    // You can omit the encoding if you know that the file uses the default platform encoding
    val source = Source.fromFile("/tmp/tmp.log", "utf-8")
    val source1 = Source.fromURL("http://horstmann.com", "UTF-8")
    val source2 = Source.fromString("Hello, World!") // Reads from the given string—useful for debugging
    val source3 = Source.stdin // Reads from standard input

    //    for (c <- source)       // c is a char
    //      println(c)

//    val lineIterator = source.getLines()
//    for (l <- lineIterator)     //l is a string
//      println(l)

//    val linesArray = source.getLines().toArray
//    println("linesArray: " + linesArray)
//    for (a <- linesArray)
//      println("a is: " + a)

    val contents = source.mkString // the whole content as a String
    println("contents: " + contents)
    source.close()        // close
  }

  def cacheRead() = {
    val source = Source.fromFile("/tmp/tmp.log", "utf-8")
    val iter = source.buffered        //  cache
    while (iter.hasNext) {
      if (iter.head == 'b') {
        println(iter.next())
      } else {
        iter.next
      }
    }

    source.close()
  }

  // 读文件，分成一个一个单词，并转为数字
  def readFromFileToWordAndNumber(): Unit = {
    val source = Source.fromFile("/tmp/tmp.log", "utf-8")
    val tokens = source.mkString.split("\\s+")
    println("tokens: " + tokens)

    // 1 method for yield
    val numbers = for (w <- tokens) yield w.toDouble
    println("numbers: " + numbers)

    // 2 method for map
    val number2 = tokens.map(_.toDouble)
    source.close()
  }

  // 读取二进制文件
  def readFromBinFile() = {
    import java.io.File
    import java.io.FileInputStream

    val file = new File("myfile")
    val in = new FileInputStream(file)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes)
    in.close()
  }

  // 写入文本文件
  def writeFromTextFile() = {
    import java.io.PrintWriter
    val out = new PrintWriter("numbers.txt")
    for (i <- 1 to 100) out.println(i)
    out.close()

    // use string format
    val quantity = 100
    val price = .1
    out.print("%6d %10.2f".format(quantity, price))
  }

  // 用递归的方式遍历所有子目录
  def subdirs(dir: File): Iterator[File] = {
    val childDir = dir.listFiles().filter(_.isDirectory)
    childDir.toIterator ++ childDir.toIterator.flatMap(subdirs _)


    /**
    // Print all subdirectories with the call
    import java.io.File
    val dir:File = new File("/tmp")
    // public static Path walkFileTree(Path start, FileVisitor<? super Path> visitor)
    // Here implicit conversion adapts a function to the interface(FileVisitor)
    Files.walkFileTree(dir.toPath, (f: Path) => println(f))
      */
  }

  // 序列化
  /**
    * class Person extends Serializable

object Run extends App {
  val fred = new Person
  import java.io._
  val out = new ObjectOutputStream(new FileOutputStream("/tmp/test.obj"))
  out.writeObject(fred)
  out.close()
  val in = new ObjectInputStream(new FileInputStream("/tmp/test.obj"))
  val savedFred = in.readObject().asInstanceOf[Person]
}
    */
}
