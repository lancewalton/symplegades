package symplegades.show

import _root_.argonaut.Json
import scalaz.Show
import symplegades.JsonUtils._

object SpecUtils {
  val dummyJson = parse("true")

  val jShow = new Show[Json] {
    override def shows(json: Json) = json.toString
  }

  implicit val pathAlg = new ShowPathAlg {}

  implicit val filterAlg = new ShowFilterAlg[Json] {
    val jsonShow = jShow
  }

  implicit val transformAlg = new ShowTransformAlg[Json] {
    val jsonShow = jShow
  }
}