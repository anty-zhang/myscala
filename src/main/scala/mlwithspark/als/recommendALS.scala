package mlwithspark.als

/**
  * Created by xiaoqiang on 16-6-19.
  * 1. 使用ALS做推荐
  * 2. 使用org.jblas做科学计算
  */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.mllib.recommendation.ALS
import org.jblas.DoubleMatrix

object recommendALS {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("recommendALS").setMaster("local[5]")
    val sc = new SparkContext(conf)

    val rawData = sc.textFile("/home/xiaoqiang/Documents/ml-100k/u.data")
    println(rawData.first())
    /**
      * 196	242	3	881250949
      */

    // 将时间戳去掉
    // rawRatings = RDD[user, movie, rating]
    val rawRatings = rawData.map(_.split("\t").take(3))
    println(rawRatings.first().mkString(","))
    /**
      * 196,242,3
      */

    // 构造Rating类
    val ratings = rawRatings.map{ case Array(user, movie, rating) =>
      Rating(user.toInt, movie.toInt, rating.toDouble)
    }
    println(ratings.first())
    /**
      * Rating(196,242,3.0)
      */
    /////////////////////////////////////////////////////////////////////////////////////
    // 训练推荐模型
    // rank：对应ALS模型中因子的个数，也就是在低阶近似矩阵中隐含特征个数
    //      在实践中该参数通常作为训练效果和系统开销之间的调节参数，合理取值为10到200
    // iterations： 运行时迭代次数。 ALS能确保每次迭代都能降低评级矩阵的重建误差，但一般经过稍次迭代后
    //       ALS模型便已能收敛为一个比较合理的好模型
    // lambda： 控制模型正则化过程，从而控制模型过拟合情况。
    //      值越高，正则化越厉害，其赋值与实际数据大小、特征和稀疏程度有关系
    // trainImplicit: 隐式数据训练
    /////////////////////////////////////////////////////////////////////////////////////
    val model = ALS.train(ratings, 50, 10, 0.01)      // train

    println(model.userFeatures.count())
    println(model.productFeatures.count())
    /**
      * 943
      * 1682
      */

    /////////////////////////////////////////////////////////////////////////////////////
    // 使用推荐模型
    // 1. 为用户推荐物品
    // 2. 校验推荐内容： 简单对比用户评级过的电影标题和被推荐的那些电影名称
    /////////////////////////////////////////////////////////////////////////////////////
    // 为单个的user，movie评分
    val predictedRating = model.predict(789, 123)
    println("predictedRating: " + predictedRating)
    /**
      * predictedRating: 4.753251093211194
      */

    // 为某个用户生成前K个推荐物品
    val userId = 789
    val K = 10
    val topKRecs = model.recommendProducts(userId, K)     // recommendProducts
    println(topKRecs.mkString("\n"))
    /**
      * Rating(789,573,5.953529258118168)
        Rating(789,185,5.622434433182057)
        Rating(789,603,5.514032643130571)
        Rating(789,484,5.36217617363661)
        Rating(789,56,5.351563488836356)
        Rating(789,12,5.277976091699702)
        Rating(789,176,5.243054633092565)
        Rating(789,217,5.174126763712592)
        Rating(789,169,5.135628154200901)
        Rating(789,482,5.072270540240983)
      */

    // 校验推荐内容
    // 简单对比用户评级过的电影标题和被推荐的那些电影名称
    // 用户评级过的电影名称
    val movies = sc.textFile("/home/xiaoqiang/Documents/ml-100k/u.item")
    val titles = movies.map(line=> line.split("\\|").take(2))
                    .map(array=> (array(0).toInt, array(1)))
                    .collectAsMap()                           // collectMap

    /**
      * keyBy(f)返回以f为key的元组
      */
    val moviesForUser = ratings.keyBy(_.user).lookup(789)     // keyBy   lookup
    println("====用户评级过的电影名称====")
    moviesForUser.sortBy(-_.rating).take(10).map{rating =>    // sortBy(-)
      (titles(rating.product), rating.rating)
    }.foreach(println)

    println("====给用户推荐的前10个电影名称====")

    /**
      * ====用户评级过的电影名称====
      (Godfather, The (1972),5.0)
      (Trainspotting (1996),5.0)
      (Dead Man Walking (1995),5.0)
      (Star Wars (1977),5.0)
      (Swingers (1996),5.0)
      (Leaving Las Vegas (1995),5.0)
      (Bound (1996),5.0)
      (Fargo (1996),5.0)
      (Last Supper, The (1995),5.0)
      (Private Parts (1997),4.0)
      ====给用户推荐的前10个电影名称====
      */
    // 给用户推荐的前10个电影名称
    topKRecs.map{rating =>
      (titles(rating.product), rating.rating)
    }.foreach(println)

    /////////////////////////////////////////////////////////////////////////////////////
    // 使用推荐模型
    // 物品推荐
    /////////////////////////////////////////////////////////////////////////////////////
    // 举例： 求物品567的相似度
    val itemId = 567
    val itemFactor = model.productFeatures.lookup(itemId).head
    itemFactor.foreach(println)
    val itemVector = new DoubleMatrix(itemFactor)
    println(cosineSimilarity(itemVector, itemVector))

    // 求各个物品的余弦相似度
    val sims = model.productFeatures.map{case (id, factor) =>
      val factorVector = new DoubleMatrix(factor)   // DoubleMatrix
      val sim = cosineSimilarity(factorVector, itemVector)
      (id, sim)
    }
    // 按着相似度排序，找出与物品567最相似的前10个物品
    // Ordering 的使用
    val sortedSims = sims.top(K)(Ordering.by[(Int, Double), Double] {
      case (id, similarity) => similarity
    })
    println(sortedSims.take(10).mkString("\n"))

    /**
      * (567,1.0)
        (201,0.6989209387155285)
        (436,0.6891795783001057)
        (24,0.6853937260076525)
        (670,0.6813818181229916)
        (952,0.6739251800041843)
        (53,0.6715331046236486)
        (219,0.6711967444354913)
        (91,0.6699811527297995)
        (7,0.6698625232900965)
      */

    // 检查物品的相似度
    println("给定电影的名称： " + titles(itemId))
    /**
      * 给定电影的名称： Wes Craven's New Nightmare (1994)
      */
    // 排除第一部自身电影，去1-11部定影，打印电影名字和相似度
    val sortedSims2 = sims.top(K + 1)(Ordering.by[(Int, Double), Double] {
      case (id, similarity) => similarity
    })

    println(sortedSims2.slice(1, 11).map{ case (id, sim) =>
      (titles(id), sim)
    }.mkString("\n"))

    /**
      * (Evil Dead II (1987),0.6989209387155285)
      (American Werewolf in London, An (1981),0.6891795783001057)
      (Rumble in the Bronx (1995),0.6853937260076525)
      (Body Snatchers (1993),0.6813818181229916)
      (Blue in the Face (1995),0.6739251800041843)
      (Natural Born Killers (1994),0.6715331046236486)
      (Nightmare on Elm Street, A (1984),0.6711967444354913)
      (Nightmare Before Christmas, The (1993),0.6699811527297995)
      (Twelve Monkeys (1995),0.6698625232900965)
      (Army of Darkness (1993),0.6693148155437542)
      */

    /////////////////////////////////////////////////////////////////////////////////////
    // 推荐模型效果评估
    // 1. 均方差： 用于显示评级，各平方误差和与总数的商，平方误差为预测值与真实值之间差的平方
    // 2. MAPK： K值平均准确率，整个数据集上的K值平均个准确率(APK)的均值。可用于隐士推荐评价。
    //      APK：对于每次查询，将前结果中前K个与实际相关的文档进行比较，结果中文档的实际相关性越高且排名也靠前，则APK分值越高。
    //          主要用来衡量针对某个查询所返回的“前K个”文档的平均相关性。
    // 3. 使用MLlib内置评估函数
    /////////////////////////////////////////////////////////////////////////////////////

    // 1. 均方差评估
    // 从ratingRDD中提取user，product ID
    val userProducts = ratings.map{ case Rating(user, product, rating) =>
      (user, product)
    }

    val predictions = model.predict(userProducts).map{ case Rating(user, product, rating) =>
      ((user, product), rating)
    }

    val ratingsAndPredictions = ratings.map{case Rating(user, product, rating) =>
      ((user, product), rating)
    }.join(predictions)

    println (ratingsAndPredictions.first())

    /**
      * ((userId, productId), (rating, predictRating))
      * ((928,168),(5.0,4.992668718139719))
      */

    val MES = ratingsAndPredictions.map{
      case ((user, product), (actual, predicted)) =>
        math.pow(actual - predicted, 2)
    }.reduce(_ + _) / ratingsAndPredictions.count()
    println("Mean Squared Error = " + MES)
    /**
      * Mean Squared Error = 0.08585123057527161
      */


    // 2. MAPK： K值平均准确率
    // 实例1： 用户789推荐的前十个电影(topKRecs)的MAPK值的计算
    val actualMovies = moviesForUser.map(_.product)
    val predictedMovies = topKRecs.map(_.product)
    println("actualMovies: " + actualMovies)
    println("predictedMovies: " + predictedMovies.toSeq)
    /**
      * actualMovies: ArrayBuffer(1012, 127, 475, 93, 1161, 286, 293, 9, 50, 294, 181, 1, 1008, 508, 284, 1017, 137, 111, 742, 248, 249, 1007, 591, 150, 276, 151, 129, 100, 741, 288, 762, 628, 124)
        predictedMovies: predictedMovies: WrappedArray(179, 675, 7, 156, 611, 479, 214, 653, 959, 42)
      */
    val apk10 = avgPrecisionK(actualMovies, predictedMovies, 10)
    println("apk10: " + apk10)
    /**
      * apk10: 0.0
      */

    // 实例2： 全局MAPK
    // itemFactors: 分解后的物品的维度数据，构建DoubleMatrix矩阵并且广播出去
    val itemFactors = model.productFeatures.map{ case(id, factor) =>
      factor
    }.collect()
    println("itemFactors: " + itemFactors.head.map(println))
    val itemMatrix = new DoubleMatrix(itemFactors)
    println("rows: " + itemMatrix.rows + ", columns: " + itemMatrix.columns)
    /**
      * (rows: 1682, columns: 50)
      */
    val imBroadcast = sc.broadcast(itemMatrix)

    // 计算每个用户的推荐文章，并按着分数进行降序排序
    val allRecs = model.userFeatures.map{case (userId, factor) =>
      val userVector = new DoubleMatrix(factor)
      // scores: 1682 * 1 矩阵维度
      val scores = imBroadcast.value.mmul(userVector)   // mmul 表示矩阵相乘

      val sortedWithId = scores.data.zipWithIndex.sortBy(-_._1)
      // recommendIds = Seq[withIndexId]    文章索引列表？ TODO这个有问题需要改成文章ID
      val recommendIds = sortedWithId.map(_._2 + 1).toSeq
      (userId, recommendIds)
    }

    // 获得每个用户的实际文章列表
    val userMovies = ratings.map{ case Rating(user, product, rating) =>
      (user, product)
    }.groupBy(_._1)

    val MAPK = allRecs.join(userMovies).map{ case (userId, (predicted, actualWithIds)) =>
      val actual = actualWithIds.map(_._2).toSeq
      avgPrecisionK(actual, predicted, 10)
    }.reduce(_ + _) / allRecs.count()
    println("MAPK is: " + MAPK)
    /**
      * MAPK is: 0.023443628069821056
      */


    // 3. 使用MLib内置评估函数
    // MSE RMSE
    import org.apache.spark.mllib.evaluation.RegressionMetrics
    val predictedAndTrue = ratingsAndPredictions.map{ case ((user, product), (actual, predicted)) =>
      (actual, predicted)
    }
    val regressionMetrics = new RegressionMetrics(predictedAndTrue)
    println("MLib Mean Squared Error: " + regressionMetrics.meanSquaredError)
    println("MLib Root Mean Squared Error: " + regressionMetrics.rootMeanSquaredError)
    /**
      * MLib Mean Squared Error: 0.08462475201409742
        MLib Root Mean Squared Error: 0.29090333792189016
      */

    // MAPK
    import org.apache.spark.mllib.evaluation.RankingMetrics
    val predictedAndTrueForRanking = allRecs.join(userMovies).map{ case (userId, (predicted, actualWithIds)) =>
      val actual = actualWithIds.map(_._2)
      (predicted.toArray, actual.toArray)
    }
    val rankingMetrics = new RankingMetrics(predictedAndTrueForRanking)
    println("Mean Average Precision: " + rankingMetrics.meanAveragePrecision)
    /**
      * Mean Average Precision: 0.06767126929950329
      */

    // 当K值很大时，全局平均准确率大体相同
    // Compare to our implementation, using K = 2000 to approximate the overall MAP
    val MAPK2000 = allRecs.join(userMovies).map{ case (userId, (predicted, actualWithIds)) =>
      val actual = actualWithIds.map(_._2).toSeq
      avgPrecisionK(actual, predicted, 2000)
    }.reduce(_ + _) / allRecs.count
    println("Mean Average Precision = " + MAPK2000)
    /**
      * Mean Average Precision = 0.06767126929950326
      */
  }

  /**
    * Code for this function is based on: https://github.com/benhamner/Metrics (衡量指标for python)
    * @param actual 某个用户阅读的实际文档列表
    * @param predicted  某个用户的预测文档列表
    * @param k  前K值
    * @return
    */
  def avgPrecisionK(actual: Seq[Int], predicted: Seq[Int], k: Int): Double = {
    val predK = predicted.take(k)
    var score = 0.0
    var numHits = 0.0
    for ((item, index) <- predK.zipWithIndex) {
      if (actual.contains(item) ) {
        numHits += 1.0
        score += numHits / (index.toDouble + 1.0)
      }
    }

    if (actual.isEmpty) {
      1.0
    } else {
      score / scala.math.min(actual.size, k).toDouble
    }
  }

  /**
    * desc: 相似度计算
    *   相似度类型：余弦相似度，皮尔森相关系数，针对二元向量的杰卡德相关系数
    *   余弦相似度： 是两个向量在n维空间里两者夹角的度数。它等于两个向量的点积与各向量范数乘积的商
    * @param vec1
    * @param vec2
    * @return
    */
  def cosineSimilarity(vec1: DoubleMatrix, vec2: DoubleMatrix): Double = {
    vec1.dot(vec2) / (vec1.norm2() * vec2.norm2())
  }
}
