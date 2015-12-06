package symplegades.transform

import symplegades.path.Path
import scalaz.syntax.show._
import scalaz.std.string._
import scalaz.Show

class ShowTransformAlg[Json: Show] extends TransformAlg[String, String, Json] {
  val noop = "Noop"
  def delete(path: Path[String]) = s"Delete(${path.shows})"
  def insert(path: Path[String], toInsert: Json) = s"Insert(${path.shows}, ${toInsert.shows})"
  def copy(from: Path[String], to: Path[String]) = s"Copy(${from.shows}, ${to.shows})"
  def move(from: Path[String], to: Path[String]) = s"Move(${from.shows}, ${to.shows})"
  def replaceValue(path: Path[String], replacement: Json) = s"ReplaceValue(${path.shows}, ${replacement.shows})"
}