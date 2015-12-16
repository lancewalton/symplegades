package symplegades.show

import symplegades.core.path.Path
import scalaz.syntax.show._
import scalaz.std.string._
import scalaz.Show
import symplegades.core.path.NonRootPath
import symplegades.core.transform.TransformAlg

trait ShowTransformAlg[Json] extends TransformAlg[Json, String, String] {
  implicit def jsonShow: Show[Json]
  
  val noop = (_: Json) => "Noop"
  def delete(path: NonRootPath[String]) = _ => s"Delete(${path.shows})"
  def insert(path: NonRootPath[String], toInsert: Json) = _ => s"Insert(${path.shows}, ${toInsert.shows})"
  def copy(from: Path[String], to: NonRootPath[String]) = _ => s"Copy(${from.shows}, ${to.shows})"
  def move(from: NonRootPath[String], to: NonRootPath[String]) = _ => s"Move(${from.shows}, ${to.shows})"
  def replaceValue(path: Path[String], replacement: Json) = _ => s"ReplaceValue(${path.shows}, ${replacement.shows})"
  def map(path: Path[String], f: Json => String) = json => s"Map(${path.shows}, ${f(json)}"
}