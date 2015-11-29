package symplegades.filter

import argonaut._
import Argonaut._
import symplegades.path.Path

object ArgonautCursorFilterAlg extends FilterAllAlg[Filter, Cursor => Option[Cursor]] {
  val allPass = new Filter { def apply(cursor: Cursor) = true }
  val noPass = not(allPass)
  def or(lhs: Filter, rhs: Filter) = new Filter { def apply(cursor: Cursor) = lhs(cursor) || rhs(cursor) }
  def and(lhs: Filter, rhs: Filter) = not(or(not(lhs), not(rhs)))
  def not(filter: Filter) = new Filter { def apply(cursor: Cursor) = !filter(cursor) }
  def hasNode(path: Path[Cursor => Option[Cursor]]) = new Filter { def apply(cursor: Cursor) = navigatePath(path, cursor).isDefined }
  def hasBooleanValue(path: Path[Cursor => Option[Cursor]], value: Boolean) = new Filter { def apply(cursor: Cursor) = navigatePath(path, cursor).exists(_.focus.bool.exists(_ == value)) }
  
  def navigatePath(path: Path[Cursor => Option[Cursor]], cursor: Cursor): Option[Cursor] =
    path.path.list.foldLeft(Option(cursor)) { (acc, e) => acc.flatMap(e) }
}
