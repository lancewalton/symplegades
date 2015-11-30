package symplegades.argonaut

import scala.language.implicitConversions

import argonaut.{ Cursor, Jsons }
import symplegades.path.{ Path, PathAlg }

object ArgonautPathAlg extends PathAlg[CursorToOptionalCursor] with Jsons {
  def path(field: String) = Path.asPath((x: Cursor) => (x downField field))
  def /(path: PathType, field: JsonField) = path andThen ((x: Cursor) => (x downField field))
}

