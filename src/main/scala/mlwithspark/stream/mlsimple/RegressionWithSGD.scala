package mlwithspark.stream.mlsimple

import breeze.linalg.DenseVector
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.mllib.regression.{StreamingLinearRegressionWithSGD, LabeledPoint}
import org.apache.spark.mllib.linalg.Vectors
/**
  * Created by xiaoqiang on 16-6-5.
  */
object RegressionWithSGD {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[5]", "RegressionWithSGD", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)

    val NumFeatures = 100       // 特征个数
    //  创建0向量作为流回归模型的初始权值向量
    val zeroVector = DenseVector.zeros[Double](NumFeatures)

    // 创建模型
    val model = new StreamingLinearRegressionWithSGD()
              .setInitialWeights(Vectors.dense(zeroVector.data))      // init
              .setNumIterations(1)              // 设置迭代次数
              .setStepSize(0.01)                // 这是步长


    // create a stream of labeled points
    // h创建一个标签点的流
    val labeledStream = stream.map{ event=>
      val split = event.split("\t")
      val y = split(0).toDouble
      val features = split(1).split(",").map(_.toDouble)

      LabeledPoint(label = y, features = Vectors.dense(features))
    }

    // train and test model on the stream
    // and print predictions for illustrative purposes
    // 通知模型在转换后的DStream上做训练，并测试输出的DStream每一批数据
    // 前几个元素的预测值
    model.trainOn(labeledStream)
//    model.predictOn(labeledStream.).print()


    ssc.start()
    ssc.awaitTermination()
  }
}
