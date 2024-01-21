package forex.util

object OneFrame {

  def createUri(host: String, port: String, pairs: List[String]): String = {
    val pairsQueryString = pairs.mkString("&pair=", "&pair=", "")
    s"http://$host:$port/rates?$pairsQueryString"
  }

}
