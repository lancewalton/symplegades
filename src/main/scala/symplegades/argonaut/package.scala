package symplegades

import _root_.argonaut.Json
import scalaz.{ @?> => @?>, PLensFamily, \/ => \/ }
import scalaz.syntax.std.option.ToOptionOpsFromOption
import symplegades.core.filter.Filter
import symplegades.argonaut.{ PathElement }
import symplegades.core.transform.{ Transform, TransformFailure }
import symplegades.core.path.{ NonRootPath, Path, RootPath }

package object argonaut {
  type PathType = Path[PathElement]
  type JsonFilter = Filter[Json]
  type JsonTransform = Transform[Json]
  type JsonTransformFailure = TransformFailure[Json]
  
  private[argonaut] def composePath(path: PathType): Json @?> Json = path match {
    case RootPath => PLensFamily.plensFamilyId
    case NonRootPath(p) => p.list.map(_.lens).reduceLeft(_ >=> _)
  }
  
  implicit class OptionSyntax[T](o: Option[T]) {
    def swap[V](v: ⇒ V): Option[V] = o.fold(Option(v))(_ ⇒ None)
    def orFail(operation: String, msg: String, json: Json): \/[JsonTransformFailure, T] = o.toRightDisjunction(TransformFailure(s"$operation: $msg", json))
  }
}