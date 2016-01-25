package symplegades

import _root_.argonaut.Json
import cats.data.Xor
import scalaz.{ @?> => @?>, PLensFamily }

import symplegades.core.filter.Filter
import symplegades.core.transform.{ Transform, TransformFailure }
import symplegades.core.path.{ NonRootPath, Path, RootPath }

import cats.std.list._
import cats.syntax.option._

package object argonaut {
  type PathType = Path[PathElement]
  type JsonFilter = Filter[Json]
  type JsonTransformFailure = TransformFailure[Json]
  type JsonTransformResult = Xor[JsonTransformFailure, Json]
  type JsonTransform = Transform[Json, JsonTransformResult]

  private[argonaut] def composePath(path: PathType): Json @?> Json = path match {
    case RootPath => PLensFamily.plensFamilyId
    case NonRootPath(p) => p.unwrap.map(_.lens).reduceLeft(_ >=> _)
  }

  implicit class OptionSyntax[T](o: Option[T]) {
    def swap[V](v: ⇒ V): Option[V] = o.fold(Option(v))(_ ⇒ None)
    def orFail(operation: String, msg: String, json: Json): Xor[JsonTransformFailure, T] = o.toRightXor(TransformFailure(s"$operation: $msg", json))
  }
}
