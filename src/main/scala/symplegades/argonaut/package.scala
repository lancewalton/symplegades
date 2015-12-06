package symplegades

import _root_.argonaut.{Cursor, Json}
import symplegades.path.Path
import scalaz.@?>
import symplegades.path.RootPath
import symplegades.path.NonRootPath
import scalaz.PLensFamily

package object argonaut {
  type PathType = Path[PathElement]
  type Transform = Json => Option[Json]
  
  private[argonaut] def composePath(path: PathType): Json @?> Json = path match {
    case RootPath => PLensFamily.plensFamilyId
    case NonRootPath(p) => p.list.map(_.lens).reduceLeft(_ >=> _)
  }
  
  implicit class OptionSyntax[T](o: Option[T]) {
    def swap[V](v: => V) = o.fold(Option(v))(_ => None)
  }
}