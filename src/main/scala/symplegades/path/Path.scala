package symplegades.path

import scala.language.implicitConversions

import scalaz.{ NonEmptyList, Show }
import scalaz.syntax.show.ToShowOps

case class Path[E](path: NonEmptyList[E]) {
  def andThen[E2 >: E](e: E2) = Path((e <:: path.reverse).reverse)
}

object Path {
  implicit def asPath[E](e: E): Path[E] = Path(NonEmptyList(e))

  implicit class PathSyntax[E: PathAlg](p: Path[E]) {
    def /(field: String): Path[E] = implicitly[PathAlg[E]] / (p, field)
  }
  
  implicit def show[E](implicit eShow: Show[E]) = new Show[Path[E]] {
    override def shows(path: Path[E]) = path.path.map(_.shows).list.mkString("/")
  }
}