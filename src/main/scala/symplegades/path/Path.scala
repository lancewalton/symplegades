package symplegades.path

import scalaz.NonEmptyList
import scala.language.implicitConversions

case class Path[E](path: NonEmptyList[E]) {
  def andThen[E2 >: E](e: E2) = Path((e <:: path.reverse).reverse)
}

object Path {
  implicit def asPath[E](e: E): Path[E] = Path(NonEmptyList(e))

  implicit class PathSyntax[E: PathAlg](p: Path[E]) {
    def /(field: String): Path[E] = implicitly[PathAlg[E]] / (p, field)
  }
}