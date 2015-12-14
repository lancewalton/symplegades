package symplegades.argonaut

import scala.language.implicitConversions

import argonaut._
import Argonaut._
import symplegades.core.path.{ Path, PathAlg }

object ArgonautPathAlg extends PathAlg[PathElement] with Jsons {
	def element(field: JsonField) = PathElement(jObjectPL >=> jsonObjectPL(field), field)
}

