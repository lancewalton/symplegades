package symplegades.filter

import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.value.Value
import symplegades.value.FalseValue
import symplegades.value.TrueValue
import symplegades.value.NumberValue
import scalaz.syntax.equal._
import scalaz.syntax.applicative._
import scalaz.std.option._

object ArgonautCursorFilterAlg extends FilterAllAlg[Filter, Cursor => Option[Cursor]] {
  val allPass = new Filter { def apply(cursor: Cursor) = true }
  val noPass = not(allPass)
  def or(lhs: Filter, rhs: Filter) = new Filter { def apply(cursor: Cursor) = lhs(cursor) || rhs(cursor) }
  def and(lhs: Filter, rhs: Filter) = not(or(not(lhs), not(rhs)))
  def not(filter: Filter) = new Filter { def apply(cursor: Cursor) = !filter(cursor) }
  def hasNode(path: Path[Cursor => Option[Cursor]]) = new Filter { def apply(cursor: Cursor) = navigatePath(path, cursor).isDefined }
  def hasValue(path: Path[Cursor => Option[Cursor]], value: Value) = new Filter { def apply(cursor: Cursor) = navigatePath(path, cursor).exists { c =>
    val v = c.focus
    value match {
      case FalseValue => v.bool.exists(!_)
      case TrueValue => v.bool.exists(identity)
      case NumberValue(n) => (v.number |@| JsonNumber.fromString(n)) { _ ≟ _ } exists(identity)
    } } }
  
  def navigatePath(path: Path[Cursor => Option[Cursor]], cursor: Cursor): Option[Cursor] =
    path.path.list.foldLeft(Option(cursor)) { (acc, e) => acc.flatMap(e) }
}
