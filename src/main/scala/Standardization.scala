
import com.sun.deploy.panel.NetworkSettingsDialog
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.{SparkContext, SparkConf}

import org.apache.spark.sql.SQLContext

/**
  * Created by hadoop on 5/10/16.
  */
object Standardization {
  def main(args: Array[String]) {
    if (args.length < 3) {
      System.err.println("Usage:<input> <output>")
      System.exit(1)
    }

    val conf = new SparkConf().setAppName("Standardizationtest")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    System.setProperty("spark.scheduler.mode", "FAIR")
    val sc = new SparkContext(conf)

    val data = sc.textFile(args(0))

    val youhui_level_1 = data.filter(x => x.split("\t")(2) == "yh").filter(x => x.split("\t")(3) == "1")
    val youhui_level_2 = data.filter(x => x.split("\t")(2) == "yh").filter(x => x.split("\t")(3) == "2")
    val youhui_level_3 = data.filter(x => x.split("\t")(2) == "yh").filter(x => x.split("\t")(3) == "3")
    val youhui_level_4 = data.filter(x => x.split("\t")(2) == "yh").filter(x => x.split("\t")(3) == "4")
    val yuanchuang_level = data.filter(x => x.split("\t")(2) == "yc").filter(x => x.split("\t")(3) == "1")
    //yuanchuang_level.saveAsTextFile(args(5)+"debug")

    val youhui_id_1 = youhui_level_1.map(x => x.split("\t")(4))
    val youhui_id_2 = youhui_level_2.map(x => x.split("\t")(4))
    val youhui_id_3 = youhui_level_3.map(x => x.split("\t")(4))
    val youhui_id_4 = youhui_level_4.map(x => x.split("\t")(4))
    val yuanchuang_id = yuanchuang_level.map(x => x.split("\t")(4))

    val youhui_1 = youhui_id_1.distinct().collect()
    val youhui_2 = youhui_id_2.distinct().collect()
    val youhui_3 = youhui_id_3.distinct().collect()
    val youhui_4 = youhui_id_4.distinct().collect()
    val yuanchuang_1 = yuanchuang_id.distinct().collect()

    data.cache()
    youhui_level_1.cache()
    youhui_level_2.cache()
    youhui_level_3.cache()
    youhui_level_4.cache()
    yuanchuang_level.cache()

    youhui_id_1.cache()
    youhui_id_2.cache()
    youhui_id_3.cache()
    youhui_id_4.cache()
    yuanchuang_id.cache()

    val output = youhui_1.par.foreach(i => {
      val lines = youhui_level_1.filter(a => a.split("\t")(4) == i)
      val line = lines.map(x => x.split("\t")(5))
      val trainningData = line.map(a => Vectors.dense(a.toString.toDouble))
      val scaler = new StandardScaler(withMean = true, withStd = true).fit(trainningData)
      val scaledVectors = trainningData.map(v => scaler.transform(v))
      val str1 = lines zip (scaledVectors)

//      str1.saveAsTextFile(args(1) + i)
      str1
    })




    youhui_2.par.foreach(i => {
      val lines = youhui_level_2.filter(a => a.split("\t")(4) == i)
      val line = lines.map(x => x.split("\t")(5))
      val trainningData = line.map(a => Vectors.dense(a.toString.toDouble))
      val scaler = new StandardScaler(withMean = true, withStd = true).fit(trainningData)
      val scaledVectors = trainningData.map(v => scaler.transform(v))
      val str1 = lines zip (scaledVectors)

      str1.saveAsTextFile(args(2) + i)
    })
    youhui_3.par.foreach(i => {
      val lines = youhui_level_3.filter(a => a.split("\t")(4) == i)
      val line = lines.map(x => x.split("\t")(5))
      val trainningData = line.map(a => Vectors.dense(a.toString.toDouble))
      val scaler = new StandardScaler(withMean = true, withStd = true).fit(trainningData)
      val scaledVectors = trainningData.map(v => scaler.transform(v))
      val str1 = lines zip (scaledVectors)

      str1.saveAsTextFile(args(3) + i)
    })
    youhui_4.par.foreach(i => {
      val lines = youhui_level_4.filter(a => a.split("\t")(4) == i)
      val line = lines.map(x => x.split("\t")(5))
      val trainningData = line.map(a => Vectors.dense(a.toString.toDouble))
      val scaler = new StandardScaler(withMean = true, withStd = true).fit(trainningData)
      val scaledVectors = trainningData.map(v => scaler.transform(v))
      val str1 = lines zip (scaledVectors)

      str1.saveAsTextFile(args(4) + i)
    })
    yuanchuang_1.par.foreach(i => {
      val lines = yuanchuang_level.filter(a => a.split("\t")(4) == i)
      //lines.saveAsTextFile(args(5)+"debug")
      val line = lines.map(x => x.split("\t")(5))
      val trainningData = line.map(a => Vectors.dense(a.toString.toDouble))
      val scaler = new StandardScaler(withMean = true, withStd = true).fit(trainningData)
      val scaledVectors = trainningData.map(v => scaler.transform(v))
      val str1 = lines zip (scaledVectors)

      str1.saveAsTextFile(args(5) + i)
    })


  }


}

