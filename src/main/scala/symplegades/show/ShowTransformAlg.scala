package symplegades.show

import symplegades.core.path.Path
import scalaz.syntax.show._
import scalaz.std.string._
import scalaz.Show
import symplegades.core.path.NonRootPath
import symplegades.core.transform.TransformAlg
import symplegades.core.transform.Transform

trait ShowTransformAlg[Json] extends TransformAlg[Json, ShowPathElement, ShowFilter, ShowTransformResult] {
  type ShowPath = Path[ShowPathElement]
  type ShowNonRootPath = NonRootPath[ShowPathElement]

  implicit def jsonShow: Show[Json]

  val identity = (_: Json) => "Identity"
  def delete(path: ShowNonRootPath) = _ => s"Delete(${path.shows})"
  def insert(path: ShowNonRootPath, toInsert: Json) = _ => s"Insert(${path.shows}, ${toInsert.shows})"
  def copy(from: ShowPath, to: ShowNonRootPath) = _ => s"Copy(${from.shows}, ${to.shows})"
  def move(from: ShowNonRootPath, to: ShowNonRootPath) = _ => s"Move(${from.shows}, ${to.shows})"
  def replaceValue(path: ShowPath, replacement: Json) = _ => s"ReplaceValue(${path.shows}, ${replacement.shows})"
  def focus(path: ShowPath, f: Trans) = json => s"Focus(${path.shows}, ${f(json)}"
  def mapArray(path: ShowPath, f: ShowTransform[Json]) = json => s"MapArray(${path.shows}, ${f(json)}"
  def conditional(filter: ShowFilter, trueTransform: Trans, falseTransform: Trans) = json => s"Conditional($filter, ${trueTransform(json)}, ${falseTransform(json)})"
  def composite(transforms: Trans*): Trans = json => s"Composite(${transforms.map(_(json)).mkString(", ")})"
}