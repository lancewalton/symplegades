package symplegades.show

import symplegades.core.path.Path
import scalaz.syntax.show._
import scalaz.std.string._
import scalaz.Show
import symplegades.core.path.NonRootPath
import symplegades.core.transform.TransformAlg
import symplegades.core.transform.Transform

trait ShowTransformAlg[Json] extends TransformAlg[Json, ShowPathElement, ShowFilter, ShowTransformResult] {
  implicit def jsonShow: Show[Json]
  
  val noop = (_: Json) => "Noop"
  def delete(path: NonRootPath[ShowPathElement]) = _ => s"Delete(${path.shows})"
  def insert(path: NonRootPath[ShowPathElement], toInsert: Json) = _ => s"Insert(${path.shows}, ${toInsert.shows})"
  def copy(from: Path[ShowPathElement], to: NonRootPath[ShowPathElement]) = _ => s"Copy(${from.shows}, ${to.shows})"
  def move(from: NonRootPath[ShowPathElement], to: NonRootPath[ShowPathElement]) = _ => s"Move(${from.shows}, ${to.shows})"
  def replaceValue(path: Path[ShowPathElement], replacement: Json) = _ => s"ReplaceValue(${path.shows}, ${replacement.shows})"
  def map(path: Path[ShowPathElement], f: ShowTransform[Json]) = json => s"Map(${path.shows}, ${f(json)}"
  def conditional(filter: ShowFilter, trueTransform: Trans, falseTransform: Trans) = json => s"Conditional($filter, ${trueTransform(json)}, ${falseTransform(json)})"
}