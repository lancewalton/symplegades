package symplegades.path

import argonaut.Json
import argonaut.Cursor
import argonaut.Jsons
import scala.language.implicitConversions
import scalaz.NonEmptyList

trait PathAlg[E] {
  def path(field: String): Path[E]
  def /(path: Path[E], field: String): Path[E]
}

object ArgonautCursorPathAlg extends PathAlg[Cursor => Option[Cursor]] with Jsons {
  type T = Cursor => Option[Cursor]
  
  def path(field: String) = Path.asPath((x: Cursor) => (x --\ field))
  def /(path: Path[T], field: JsonField) = path andThen ((x: Cursor) => (x --\ field))
}

object ShowPathAlg extends PathAlg[String] {
  def path(field: String) = Path.asPath(field)
  def /(path: Path[String], field: String) = path andThen field
}

object Test extends App {
  def makePath[E](implicit alg: PathAlg[E]): Path[E] = {
    import alg._
    import Path.PathSyntax
    
    path("foo") / "bar"
  }

  println(makePath(ShowPathAlg))
}