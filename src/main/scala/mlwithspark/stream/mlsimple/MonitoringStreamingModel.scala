package mlwithspark.stream.mlsimple

/**
  * Created by xiaoqiang on 16-6-5.
  * 一个流式回归模型用来比较两个模型的性能，输出每个批次计算后的性能统计
  */

import breeze.linalg.DenseVector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.mllib.regression.{StreamingLinearRegressionWithSGD, LabeledPoint}


object MonitoringStreamingModel {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[5]", "MonitoringStreamingModel", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)

    val NumFeatures = 100
    val zeroVector = DenseVector.zeros[Double](NumFeatures)

    // 模型1
    // 学习率为0.01
    val model1 = new StreamingLinearRegressionWithSGD()
                  .setInitialWeights(Vectors.dense(zeroVector.data))
                  .setNumIterations(1)
                  .setStepSize(0.01)

    // 模型2
    // 学习率为1.0
    val model2 = new StreamingLinearRegressionWithSGD()
                    .setInitialWeights(Vectors.dense(zeroVector.data))
                    .setNumIterations(1)
                    .setStepSize(1.0)

    // 创建一个标签点的流
    val labeledStream = stream.map{event=>
      val split = event.split("\t")
      val y = split(0).toDouble
      val features = split(1).split(",").map(_.toDouble)
      LabeledPoint(label = y, features = Vectors.dense(features))
    }

    // 在同一个流上训练两个模型
    model1.trainOn(labeledStream)
    model2.trainOn(labeledStream)

    // 使用转换算子创建包含两个模型错误率的流
    // transform using
    val predsAndTrue = labeledStream.transform{rdd =>
      val latest1 = model1.latestModel()
      val latest2 = model2.latestModel()
      rdd.map{ point =>
        val pred1 = latest1.predict(point.features)
        val pred2 = latest2.predict(point.features)
        (pred1 - point.label, pred2 - point.label)
      }
    }

    // 对每个模型每个批次，出书MES和RMES统计值
    predsAndTrue.foreachRDD{(rdd, time) =>
      val mse1 = rdd.map{case (err1, err2) => err1 * err1}.mean()
      val rmse1 = math.sqrt(mse1)
      val mse2 = rdd.map{case (err1, err2) => err2 * err2} .mean()
      val rmse2 = math.sqrt(mse2)

      println(
        s"""
           |-------------------------------------------
           |Time: $time
           |-------------------------------------------
         """.stripMargin)
      println(s"MSE current batch: Model 1: $mse1; Model 2: $mse2")
      println(s"RMSE current batch: Model 1: $rmse1; Model 2: $rmse2")
      println("...\n")
    }

    ssc.start()
    ssc.awaitTermination()
  }
}
