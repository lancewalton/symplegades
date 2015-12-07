package symplegades.argonaut

import argonaut.{ Cursor, JsonNumber }
import scalaz.std.option.optionInstance
import scalaz.syntax.applicative.ToApplyOps
import scalaz.syntax.equal.ToEqualOps
import symplegades.filter.{ Filter, FilterAllAlg }
import symplegades.path.Path
import argonaut.Json
import scalaz.Equal

object ArgonautFilterAlg extends FilterAllAlg[Filter, PathElement, Json] {
  val allPass = new Filter { def apply(json: Json) = true }

  val noPass = not(allPass)

  def or(lhs: Filter, rhs: Filter) = new Filter { def apply(json: Json) = lhs(json) || rhs(json) }

  def and(lhs: Filter, rhs: Filter) = not(or(not(lhs), not(rhs)))

  def not(filter: Filter) = new Filter { def apply(json: Json) = !filter(json) }

  private def nodeAtPathHasProperty(path: PathType, property: Json => Boolean) = new Filter { def apply(json: Json) = composePath(path).get(json).exists(property) }
  
  def hasNode(path: PathType) = nodeAtPathHasProperty(path, _ => true)

  def isObject(path: PathType) = nodeAtPathHasProperty(path, _.isObject)
  def isArray(path: PathType) = nodeAtPathHasProperty(path, _.isArray)
  def isNumber(path: PathType) = nodeAtPathHasProperty(path, _.isNumber)
  def isString(path: PathType) = nodeAtPathHasProperty(path, _.isString)
  def isBoolean(path: PathType) = nodeAtPathHasProperty(path, _.isBool)
  def isNull(path: PathType) = nodeAtPathHasProperty(path, _.isNull)

  def hasValue(path: PathType, value: Json) = hasValueInSet(path, value)

  def hasValueInSet(path: PathType, value: Json*) = new Filter {
    def apply(json: Json) = composePath(path).get(json).exists { v => value.exists { _ == v } }
  }

  def focusAndMatch(path: PathType, filter: Filter) = new Filter { def apply(json: Json) = composePath(path).get(json) exists filter }
  
  def exists(path: PathType, filter: Filter) = nodeAtPathHasProperty(path, _.array exists { _ exists(filter) })
  def forall(path: PathType, filter: Filter) = nodeAtPathHasProperty(path, _.array exists { _ forall(filter) })
}
