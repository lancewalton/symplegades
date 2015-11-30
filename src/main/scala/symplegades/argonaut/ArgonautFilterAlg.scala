package symplegades.argonaut

import argonaut.{ Cursor, JsonNumber }
import scalaz.std.option.optionInstance
import scalaz.syntax.applicative.ToApplyOps
import scalaz.syntax.equal.ToEqualOps
import symplegades.filter.{ Filter, FilterAllAlg }
import symplegades.path.Path
import symplegades.value.{ FalseValue, NumberValue, TrueValue, Value }

object ArgonautFilterAlg extends FilterAllAlg[Filter, Cursor ⇒ Option[Cursor]] {
  val allPass = new Filter { def apply(cursor: Cursor) = true }

  val noPass = not(allPass)

  def or(lhs: Filter, rhs: Filter) = new Filter { def apply(cursor: Cursor) = lhs(cursor) || rhs(cursor) }

  def and(lhs: Filter, rhs: Filter) = not(or(not(lhs), not(rhs)))

  def not(filter: Filter) = new Filter { def apply(cursor: Cursor) = !filter(cursor) }

  def hasNode(path: PathType) = new Filter { def apply(cursor: Cursor) = navigatePath(path, cursor).isDefined }

  def hasValue(path: PathType, value: Value) = hasValueInSet(path, value)

  def hasValueInSet(path: PathType, value: Value*) = new Filter {
    def apply(cursor: Cursor) = navigatePath(path, cursor).exists { c ⇒
      val v = c.focus
      value.exists {
        case FalseValue     ⇒ v.bool.exists(!_)
        case TrueValue      ⇒ v.bool.exists(identity)
        case NumberValue(n) ⇒ (v.number |@| JsonNumber.fromString(n)) { _ ≟ _ } exists (identity)
      }
    }
  }

  def focusAndMatch(path: Path[Cursor ⇒ Option[Cursor]], filter: Filter) = new Filter { def apply(cursor: Cursor) = navigatePath(path, cursor) exists filter }
}
