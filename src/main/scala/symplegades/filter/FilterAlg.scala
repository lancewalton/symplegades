package symplegades.filter

import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.path.PathAlg
import symplegades.path.ShowPathAlg
import symplegades.path.ArgonautCursorPathAlg

trait FilterAlg[E, P] {
  def allPass(): E
  def noPass(): E
  def and(lhs: E, rhs: E): E
  def or(lhs: E, rhs: E): E
  def not(filter: E): E
  def hasNode(path: Path[P]): E
}

object FilterFilterAlg extends FilterAlg[Filter, Cursor => Option[Cursor]] {
  def allPass() = new Filter { def apply(cursor: Cursor) = true }
  def noPass() = not(allPass)
  def or(lhs: Filter, rhs: Filter) = new Filter { def apply(cursor: Cursor) = lhs(cursor) || rhs(cursor) }
  def and(lhs: Filter, rhs: Filter) = not(or(not(lhs), not(rhs)))
  def not(filter: Filter) = new Filter { def apply(cursor: Cursor) = !filter(cursor) }
  def hasNode(path: Path[Cursor => Option[Cursor]]) = new Filter { def apply(cursor: Cursor) = navigatePath(path, cursor).isDefined }
  
  def navigatePath(path: Path[Cursor => Option[Cursor]], cursor: Cursor): Option[Cursor] =
    path.path.list.foldLeft(Option(cursor)) { (acc, e) => acc.flatMap(e) }
}

object ShowFilterAlg extends FilterAlg[String, String] {
  def allPass() = "All"
  def noPass() = "None"
  def or(lhs: String, rhs: String) = s"($lhs OR $rhs)"
  def and(lhs: String, rhs: String) = s"($lhs AND $rhs)"
  def not(filter: String) = s"NOT($filter)"
  def hasNode(path: Path[String]) = s"HasNode(${showPath(path)})"
  
  private def showPath(path: Path[String]) = path.path.list.mkString(".")
}

object Test extends App {
  import Path.asPath
  
  def buildFilter[E, P](implicit filterAlg: FilterAlg[E, P], pathAlg: PathAlg[P]) = {
    import filterAlg._
    import pathAlg._
    import Path.PathSyntax
    
    not(and(allPass, hasNode(path("foo") / "bar")))
  }

  println(buildFilter(ShowFilterAlg, ShowPathAlg))
  
  println(buildFilter(FilterFilterAlg, ArgonautCursorPathAlg))
}
