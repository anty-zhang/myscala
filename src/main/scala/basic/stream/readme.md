1. 在线学习
（1）思想：随着接受到的消息不断更新自己，而不是像离线训练一次一次重新训练
（2）在线和离线可以结合使用

2. Spark Streaming介绍
（1）DStream(Discretized Stream) 离散化流
    一个DStream是指一个小批量作业的序列
    每个小批量作业表示一个Spark RDD

（2）时间窗口
    窗口由窗口长度和滑动间隔定义

（3）DStream转换
    跟踪状态，类似与广播变量
    普通转换，类似于RDD操作

