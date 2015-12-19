package symplegades.argonaut

import argonaut.Json
import symplegades.core.filter.FilterAllAlg
import cats.Eq
import cats.syntax.eq._

trait ArgonautFilterAlg extends FilterAllAlg[JsonFilter, PathElement, Json] {
  implicit val jsonEqual = Eq.fromUniversalEquals[Json]

  val allPass = new JsonFilter { def apply(json: Json) = true }

  val noPass = not(allPass)

  def or(lhs: JsonFilter, rhs: JsonFilter) = new JsonFilter { def apply(json: Json) = lhs(json) || rhs(json) }

  def and(lhs: JsonFilter, rhs: JsonFilter) = not(or(not(lhs), not(rhs)))

  def not(filter: JsonFilter) = new JsonFilter { def apply(json: Json) = !filter(json) }

  private def nodeAtPathHasProperty(path: PathType, property: Json ⇒ Boolean) = new JsonFilter { def apply(json: Json) = composePath(path).getOption(json).exists(property) }

  def hasNode(path: PathType) = nodeAtPathHasProperty(path, _ ⇒ true)

  def isObject(path: PathType) = nodeAtPathHasProperty(path, _.isObject)
  def isArray(path: PathType) = nodeAtPathHasProperty(path, _.isArray)
  def isNumber(path: PathType) = nodeAtPathHasProperty(path, _.isNumber)
  def isString(path: PathType) = nodeAtPathHasProperty(path, _.isString)
  def isBoolean(path: PathType) = nodeAtPathHasProperty(path, _.isBool)
  def isNull(path: PathType) = nodeAtPathHasProperty(path, _.isNull)

  def hasValue(path: PathType, value: Json) = hasValueInSet(path, value)

  def hasValueInSet(path: PathType, value: Json*) = new JsonFilter {
    def apply(json: Json) = composePath(path).getOption(json).exists { v ⇒ value.exists { _ === v } }
  }

  def focusAndMatch(path: PathType, JsonFilter: JsonFilter) = new JsonFilter { def apply(json: Json) = composePath(path).getOption(json) exists JsonFilter }

  def exists(path: PathType, JsonFilter: JsonFilter) = nodeAtPathHasProperty(path, _.array exists { _ exists JsonFilter })
  def forall(path: PathType, JsonFilter: JsonFilter) = nodeAtPathHasProperty(path, _.array exists { _ forall JsonFilter })
}
