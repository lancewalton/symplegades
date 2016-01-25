package symplegades.argonaut

import argonaut._
import Argonaut._
import symplegades.core.path.{ Path, PathAlg }

trait ArgonautPathAlg extends PathAlg[PathElement] with Jsons {
  def field(field: JsonField): PathElement = PathElement(jObjectPL >=> jsonObjectPL(field), field)
}
