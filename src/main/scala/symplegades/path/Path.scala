package symplegades.path

import scala.language.implicitConversions
import scala.language.postfixOps

import scalaz.{ NonEmptyList, Show }
import scalaz.syntax.show.ToShowOps

case class Path[E](path: NonEmptyList[E]) {
  def andThen[E2 >: E](e: E2) = Path((e <:: path.reverse).reverse)
}

object Path {
  implicit def path[E](s: String)(implicit pe: PathAlg[E]) = Path(NonEmptyList(pe./(s)))
  
  implicit class PathExtensionSyntax[E: PathAlg](p: Path[E]) {
    def /(field: String): Path[E] = p andThen implicitly[PathAlg[E]] / field
  }
  
  implicit def show[E](implicit eShow: Show[E]) = new Show[Path[E]] {
    override def shows(path: Path[E]) = path.path.list.map(_.shows).mkString("/")
  }
}