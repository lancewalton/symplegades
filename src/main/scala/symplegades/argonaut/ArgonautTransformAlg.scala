package symplegades.argonaut

import argonaut.Cursor
import symplegades.path.Path
import symplegades.transform.TransformAlg
import symplegades.transform.TransformAlg

object ArgonautTransformAlg extends TransformAlg[CursorToOptionalCursor, CursorToOptionalCursor] {
	def noop() = (c: Cursor) => Option(c)
  
  def delete(path: Path[CursorToOptionalCursor]) =
    (c: Cursor) => navigatePath(path, c).flatMap(_.delete)
}