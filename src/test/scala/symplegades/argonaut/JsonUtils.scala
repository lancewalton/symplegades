package symplegades.argonaut

import org.scalatest.Assertions
import argonaut.Json
import argonaut.Parse

object JsonUtils extends Assertions {
  def parse(json: String): Json = Parse.parse(json.stripMargin).fold(error ⇒ fail(s"Couldn't parse JSON: $error"), identity)
}