package symplegades.argonaut

import argonaut.JsonMonocle._
import argonaut.{Json, JsonObject, Jsons}
import monocle.{Lens, Optional}
import symplegades.core.path.PathAlg

import scala.language.implicitConversions

trait ArgonautPathAlg extends PathAlg[PathElement] with Jsons {


  def jsonObjectL(field: JsonField): Lens[JsonObject, Option[Json]] =
    Lens[JsonObject, Option[Json]](v ⇒ v(field))(
      opt ⇒ json ⇒
        opt.fold(json - field)(v ⇒ json + (field, v))
    )

  def jObjectAtPL(field: JsonField): Optional[JsonObject, Json] = {
    val l = jsonObjectL(field)
    Optional[JsonObject, Json](l.get)(json ⇒ jobject ⇒
      l.set(Option(json))(jobject)
    )
  }

  // PathElement(jObjectPL >=> jsonObjectPL(field), field)
	def field(field: JsonField): PathElement = PathElement(jObjectPrism ^|-? jObjectAtPL(field), field)
}