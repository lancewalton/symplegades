package symplegades.argonaut

import argonaut.{ Cursor, JsonNumber }
import scalaz.std.option.optionInstance
import scalaz.syntax.applicative.ToApplyOps
import scalaz.syntax.equal.ToEqualOps
import symplegades.filter.{ Filter, FilterAllAlg }
import symplegades.path.Path
import symplegades.value.{ FalseValue, NumberValue, TrueValue, Value }
import argonaut.Json

object ArgonautFilterAlg extends FilterAllAlg[Filter, PathElement] {
  val allPass = new Filter { def apply(json: Json) = true }

  val noPass = not(allPass)

  def or(lhs: Filter, rhs: Filter) = new Filter { def apply(json: Json) = lhs(json) || rhs(json) }

  def and(lhs: Filter, rhs: Filter) = not(or(not(lhs), not(rhs)))

  def not(filter: Filter) = new Filter { def apply(json: Json) = !filter(json) }

  def hasNode(path: PathType) = new Filter { def apply(json: Json) = composePath(path).get(json).isDefined }

  def hasValue(path: PathType, value: Value) = hasValueInSet(path, value)

  def hasValueInSet(path: PathType, value: Value*) = new Filter {
    def apply(json: Json) = composePath(path).get(json).exists { v ⇒
      value.exists {
        case FalseValue     ⇒ v.bool.exists(!_)
        case TrueValue      ⇒ v.bool.exists(identity)
        case NumberValue(n) ⇒ (v.number |@| JsonNumber.fromString(n)) { _ ≟ _ } exists (identity)
      }
    }
  }

  def focusAndMatch(path: PathType, filter: Filter) = new Filter { def apply(json: Json) = composePath(path).get(json) exists filter }
}
