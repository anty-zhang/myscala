package basic.str

/**
  * Created by xiaoqiang on 16-8-9.
  */
object TestStr {
  def main(args: Array[String]) {
    val s = "device_id: string, user_id: string, article_id: string, article_ctime: string, " +
      "article_chanel_id: string, article_chanel_name: string, confidence: string,level1ID: string, " +
      "level1: string, level2ID: string, level2: string, level3ID: string,level3: string, level4ID: string, " +
      "level4: string, rs_id1: string, rs_id2: string,rs_id3: string, rs_id4: string, rs_id5: string, " +
      "is_delete: string, ctime: string, title: string"

    val ext = s.split(",")

    val tmp = ext.map{x =>
      val Array(field, _) = x.split(":")

      field
    }

    println(tmp.mkString(","))

  }
}
