package symplegades.argonaut

import argonaut.Json
import scalaz.Equal.equalA
import scalaz.syntax.equal.ToEqualOps
import symplegades.core.filter.FilterAllAlg

trait ArgonautFilterAlg extends FilterAllAlg[JsonFilter, PathElement, Json] {
  implicit val jsonEqual = equalA[Json]

  val allPass = new JsonFilter { def apply(json: Json) = true }

  val noPass = not(allPass)

  def or(lhs: JsonFilter, rhs: JsonFilter): JsonFilter = new JsonFilter { def apply(json: Json) = lhs(json) || rhs(json) }

  def and(lhs: JsonFilter, rhs: JsonFilter): JsonFilter = not(or(not(lhs), not(rhs)))

  def not(filter: JsonFilter): JsonFilter = new JsonFilter { def apply(json: Json) = !filter(json) }

  private def nodeAtPathHasProperty(path: PathType, property: Json ⇒ Boolean): JsonFilter =
    new JsonFilter { def apply(json: Json) = composePath(path).get(json).exists(property) }

  def hasNode(path: PathType): JsonFilter = nodeAtPathHasProperty(path, _ ⇒ true)
  def isObject(path: PathType): JsonFilter = nodeAtPathHasProperty(path, _.isObject)
  def isArray(path: PathType): JsonFilter = nodeAtPathHasProperty(path, _.isArray)
  def isNumber(path: PathType): JsonFilter = nodeAtPathHasProperty(path, _.isNumber)
  def isString(path: PathType): JsonFilter = nodeAtPathHasProperty(path, _.isString)
  def isBoolean(path: PathType): JsonFilter = nodeAtPathHasProperty(path, _.isBool)
  def isNull(path: PathType): JsonFilter = nodeAtPathHasProperty(path, _.isNull)

  def hasValue(path: PathType, value: Json): JsonFilter = hasValueInSet(path, value)

  def hasValueInSet(path: PathType, value: Json*): JsonFilter = new JsonFilter {
    def apply(json: Json) = composePath(path).get(json).exists { v ⇒ value.exists { _ ≟ v } }
  }

  def focusAndMatch(path: PathType, JsonFilter: JsonFilter): JsonFilter =
    new JsonFilter { def apply(json: Json) = composePath(path).get(json) exists JsonFilter }

  def exists(path: PathType, JsonFilter: JsonFilter): JsonFilter = nodeAtPathHasProperty(path, _.array exists { _ exists (JsonFilter) })
  def forall(path: PathType, JsonFilter: JsonFilter): JsonFilter = nodeAtPathHasProperty(path, _.array exists { _ forall (JsonFilter) })
}
