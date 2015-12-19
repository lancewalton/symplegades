package symplegades.argonaut

import argonaut.{JObject, Json, JsonObject, Jsons}
import argonaut.JsonMonocle._
import argonaut.JsonObjectMonocle._
import monocle.{Lens, POptional, Optional}

import symplegades.core.path.PathAlg
import monocle.Optional

import scala.language.implicitConversions

trait ArgonautPathAlg extends PathAlg[PathElement] with Jsons {

  def jObjectAtPL(field: JsonField) = {
    val at = jObjectAt.at(field)
    Optional(at.get)(json ⇒ at.set(Some(json)))
  }

  // PathElement(jObjectPL >=> jsonObjectPL(field), field)
	def field(field: JsonField): PathElement = PathElement(jObjectPrism ^|-? jObjectAtPL(field), field)
}