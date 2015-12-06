package symplegades

import _root_.argonaut.{Cursor, Json}
import symplegades.path.Path
import scalaz.@?>

package object argonaut {
  type PathType = Path[PathElement]
  type Transform = Json => Option[Json]
  
  private[argonaut] def composePath(path: PathType): Json @?> Json = path.path.list.map(_.lens).reduceLeft(_ >=> _)
  
  implicit class OptionSyntax[T](o: Option[T]) {
    def swap[V](v: => V) = o.fold(Option(v))(_ => None)
  }
}