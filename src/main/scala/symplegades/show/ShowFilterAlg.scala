package symplegades.show

import cats.Show
import symplegades.core.path.Path
import symplegades.core.filter.FilterAllAlg
import cats.syntax.show._
import cats.std.string._
import cats.std.list._

trait ShowFilterAlg[Json] extends FilterAllAlg[String, String, Json] {
  implicit def jsonShow: Show[Json]
  
  def allPass() = "All"
  def noPass() = "None"
  def or(lhs: String, rhs: String) = s"($lhs || $rhs)"
  def and(lhs: String, rhs: String) = s"($lhs && $rhs)"
  def not(filter: String) = s"!($filter)"
  def hasNode(path: Path[String]) = s"HasNode(${path.show})"
  def isObject(path: Path[String]) = s"IsObject(${path.show})"
  def isArray(path: Path[String]) = s"IsArray(${path.show})"
  def isNumber(path: Path[String]) = s"IsNumber(${path.show})"
  def isString(path: Path[String]) = s"IsString(${path.show})"
  def isBoolean(path: Path[String]) = s"IsBoolean(${path.show})"
  def isNull(path: Path[String]) = s"IsNull(${path.show})"
  def hasValue(path: Path[String], value: Json) = s"HasValue(${path.show}, ${value.show})"
  def hasValueInSet(path: Path[String], value: Json*) = s"HasValueInSet(${path.show}, ${value.toList.show})"
  def focusAndMatch(path: Path[String], filter: String) = s"FocusAndMatch(${path.show}, $filter)"
  def exists(path: Path[String], filter: String) = s"Exists(${path.show}, $filter)"
  def forall(path: Path[String], filter: String) = s"ForAll(${path.show}, $filter)"
}