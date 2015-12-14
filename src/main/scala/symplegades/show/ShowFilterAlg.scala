package symplegades.show

import symplegades.core.path.Path
import scalaz.syntax.show._
import scalaz.std.iterable._
import scalaz.std.string._
import scalaz.Show
import symplegades.core.filter.FilterAllAlg

case class ShowFilterAlg[Json](implicit jsonShow: Show[Json]) extends FilterAllAlg[String, String, Json] {
  def allPass() = "All"
  def noPass() = "None"
  def or(lhs: String, rhs: String) = s"($lhs || $rhs)"
  def and(lhs: String, rhs: String) = s"($lhs && $rhs)"
  def not(filter: String) = s"!($filter)"
  def hasNode(path: Path[String]) = s"HasNode(${path.shows})"
  def isObject(path: Path[String]) = s"IsObject(${path.shows})"
  def isArray(path: Path[String]) = s"IsArray(${path.shows})"
  def isNumber(path: Path[String]) = s"IsNumber(${path.shows})"
  def isString(path: Path[String]) = s"IsString(${path.shows})"
  def isBoolean(path: Path[String]) = s"IsBoolean(${path.shows})"
  def isNull(path: Path[String]) = s"IsNull(${path.shows})"
  def hasValue(path: Path[String], value: Json) = s"HasValue(${path.shows}, ${value.shows})"
  def hasValueInSet(path: Path[String], value: Json*) = s"HasValueInSet(${path.shows}, ${value.shows})"
  def focusAndMatch(path: Path[String], filter: String) = s"FocusAndMatch(${path.shows}, $filter)"
  def exists(path: Path[String], filter: String) = s"Exists(${path.shows}, $filter)"
  def forall(path: Path[String], filter: String) = s"ForAll(${path.shows}, $filter)"
}