package symplegades.show

import symplegades.core.path.Path
import scalaz.syntax.show._
import scalaz.std.string._
import scalaz.Show
import symplegades.core.path.NonRootPath
import symplegades.core.transform.TransformAlg

trait ShowTransformAlg[Json] extends TransformAlg[String, String, Json] {
  implicit def jsonShow: Show[Json]
  
  val noop = "Noop"
  def delete(path: NonRootPath[String]) = s"Delete(${path.shows})"
  def insert(path: NonRootPath[String], toInsert: Json) = s"Insert(${path.shows}, ${toInsert.shows})"
  def copy(from: Path[String], to: NonRootPath[String]) = s"Copy(${from.shows}, ${to.shows})"
  def move(from: NonRootPath[String], to: NonRootPath[String]) = s"Move(${from.shows}, ${to.shows})"
  def replaceValue(path: Path[String], replacement: Json) = s"ReplaceValue(${path.shows}, ${replacement.shows})"
  def map(path: Path[String], f: String) = s"Map(${path.shows}, $f)"
}