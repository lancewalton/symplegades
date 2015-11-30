package symplegades

import _root_.argonaut.Cursor
import symplegades.path.Path

package object argonaut {
  type CursorToOptionalCursor = Cursor => Option[Cursor]
  type PathType = Path[CursorToOptionalCursor]
  
  private[argonaut] def navigatePath(path: PathType, cursor: Cursor): Option[Cursor] =
    path.path.list.foldLeft(Option(cursor)) { (acc, e) ⇒ acc.flatMap(e) }
}