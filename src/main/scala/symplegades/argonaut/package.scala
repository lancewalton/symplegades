package symplegades

import _root_.argonaut.{Cursor, Json}
import symplegades.path.Path
import scalaz.@?>
import symplegades.path.RootPath
import symplegades.path.NonRootPath
import scalaz.PLensFamily
import scalaz.\/
import scalaz.syntax.either._
import scalaz.syntax.std.option._

package object argonaut {
  type PathType = Path[PathElement]
  type Transform = Json => \/[TransformFailure, Json]
  
  private[argonaut] def composePath(path: PathType): Json @?> Json = path match {
    case RootPath => PLensFamily.plensFamilyId
    case NonRootPath(p) => p.list.map(_.lens).reduceLeft(_ >=> _)
  }
  
  implicit class OptionSyntax[T](o: Option[T]) {
    def swap[V](v: ⇒ V): Option[V] = o.fold(Option(v))(_ ⇒ None)
    def orFail(operation: String, msg: String, json: Json): \/[TransformFailure, T] = o.toRightDisjunction(TransformFailure(s"$operation: $msg", json))
  }
}