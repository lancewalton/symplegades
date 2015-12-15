package symplegades

import org.scalatest.Assertions
import _root_.argonaut.Json
import _root_.argonaut.Parse

object JsonUtils extends Assertions {
  def parse(json: String): Json = Parse.parse(json.stripMargin).fold(error ⇒ fail(s"Couldn't parse JSON: $error"), identity)
}