package symplegades.filter

import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.path.PathAlg
import symplegades.path.ShowPathAlg
import symplegades.path.ArgonautCursorPathAlg

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
  def hasBooleanValue(path: Path[P], value: Boolean): F
}

trait FilterAllAlg[F, P] extends FilterExtremeAlg[F] with FilterLogicAlg[F] with FilterNodeAlg[F, P]

object Test extends App {
  import Path.asPath
  
  def buildFilter[F, P](implicit filterAlg: FilterAllAlg[F, P], pathAlg: PathAlg[P]) = {
    import filterAlg._
    import pathAlg._
    import Path.PathSyntax
    
    not(and(hasBooleanValue(path("foo") / "baz", true), hasNode(path("foo") / "bar")))
  }

  println(buildFilter(ShowFilterAlg, ShowPathAlg))
}
