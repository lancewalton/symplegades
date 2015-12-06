package symplegades.argonaut

import argonaut.Json
import scalaz.{ @?> => @?> }

case class PathElement(lens: Json @?> Json, field: String)
