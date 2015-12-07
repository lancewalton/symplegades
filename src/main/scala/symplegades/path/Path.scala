package symplegades.path

import scala.language.implicitConversions
import scala.language.postfixOps

import scalaz.{ NonEmptyList, Show }
import scalaz.syntax.show.ToShowOps
import scalaz.syntax.std.list._

sealed trait Path[+E] {
  def andThen[E2 >: E](e: E2): NonRootPath[E2]
}

case object RootPath extends Path[Nothing] {
  def andThen[E2](e: E2) = NonRootPath(NonEmptyList(e))
  
  implicit def show(): Show[RootPath.type] = new Show[RootPath.type] {
    override def shows(path: RootPath.type) = "<obj>"
  }
}

case class NonRootPath[E](path: NonEmptyList[E]) extends Path[E] {
  def andThen[E2 >: E](e: E2) = NonRootPath((e <:: path.reverse).reverse)
  def lastElement: E = path.last
  def removeLastElement: Path[E] = path.reverse.tail.reverse.toNel.map { NonRootPath(_) } getOrElse Path.root[E]
}

object NonRootPath {
  implicit def show[E](implicit eShow: Show[E]): Show[NonRootPath[E]] = new Show[NonRootPath[E]] {
    override def shows(path: NonRootPath[E]) = path.path.list.map(_.shows).mkString("/")
  }
}

object Path {
  def root[E](): Path[E] = RootPath.asInstanceOf[Path[E]]
  
  implicit def path[E](s: String)(implicit pe: PathAlg[E]) = NonRootPath(NonEmptyList(pe element s))
  
  implicit class PathExtensionSyntax[E: PathAlg](p: Path[E]) {
    def /(field: String): NonRootPath[E] = p andThen implicitly[PathAlg[E]].element(field)
  }
  
  implicit def show[E](implicit eShow: Show[E]): Show[Path[E]] = new Show[Path[E]] {
    import scalaz.syntax.show._
    override def shows(path: Path[E]) = path match {
      case p: RootPath.type => RootPath.show.shows(p)
      case p: NonRootPath[E] => p.shows
    }
  }

}