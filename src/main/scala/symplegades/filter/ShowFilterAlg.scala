package symplegades.filter

import symplegades.path.Path
import symplegades.value.Value
import scalaz.syntax.show._
import scalaz.std.iterable._

object ShowFilterAlg extends FilterAllAlg[String, String] {
  def allPass() = "All"
  def noPass() = "None"
  def or(lhs: String, rhs: String) = s"($lhs || $rhs)"
  def and(lhs: String, rhs: String) = s"($lhs && $rhs)"
  def not(filter: String) = s"!($filter)"
  def hasNode(path: Path[String]) = s"HasNode(${showPath(path)})"
  def hasValue(path: Path[String], value: Value) = s"HasValue(${showPath(path)}, ${value.shows})"
  def hasValueInSet(path: Path[String], value: Value*) = s"HasValueInSet(${showPath(path)}, ${value.shows})"
  def focusAndMatch(path: Path[String], filter: String) = s"FocusAndMatch(${showPath(path)}, $filter)"

  private def showPath(path: Path[String]) = path.path.list.mkString(".")
}