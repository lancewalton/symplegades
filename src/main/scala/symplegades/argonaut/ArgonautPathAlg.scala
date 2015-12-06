package symplegades.argonaut

import scala.language.implicitConversions

import argonaut._
import Argonaut._
import symplegades.path.{ Path, PathAlg }

object ArgonautPathAlg extends PathAlg[PathElement] with Jsons {
	def /(field: JsonField) = PathElement(jObjectPL >=> jsonObjectPL(field), field)
}

