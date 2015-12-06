package symplegades

import _root_.argonaut.{Cursor, Json}
import symplegades.path.Path
import scalaz.@?>

package object argonaut {
  case class PathElement(lens: Json @?> Json, field: String)
  type PathType = Path[PathElement]
  type Transformation = Json => Option[Json]
  
  private[argonaut] def composePath(path: PathType): Json @?> Json = path.path.list.map(_.lens).reduceLeft(_ >=> _)
}