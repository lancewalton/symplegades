package symplegades.argonaut

import argonaut.Json
import monocle.Optional

case class PathElement(lens: Optional[Json, Json], field: String)
