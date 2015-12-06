package symplegades

import _root_.argonaut.{Cursor, Json}
import symplegades.path.Path
import scalaz.@?>

package object argonaut {
  type PathElement = Json @?> Json
  type PathType = Path[PathElement]
  type Transformation = Json => Option[Json]
  
  private[argonaut] def composePath(path: PathType): Json @?> Json = path.path.list.reduceLeft(_ >=> _)
}