package symplegades.filter

import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.path.PathAlg
import symplegades.path.ShowPathAlg
import symplegades.path.ArgonautCursorPathAlg
import symplegades.value.Value

trait FilterExtremeAlg[F] {
  def allPass(): F
  def noPass(): F
}

trait FilterLogicAlg[F] {
  def and(lhs: F, rhs: F): F
  def or(lhs: F, rhs: F): F
  def not(filter: F): F
}

trait FilterNodeAlg[F, P] {
  def hasNode(path: Path[P]): F
  def hasValue(path: Path[P], value: Value): F
}

trait FilterAllAlg[F, P] extends FilterExtremeAlg[F] with FilterLogicAlg[F] with FilterNodeAlg[F, P]
