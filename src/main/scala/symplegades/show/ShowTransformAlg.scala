package symplegades.show

import cats.Show
import cats.syntax.show._
import cats.std.string._
import symplegades.core.path.Path

import symplegades.core.path.NonRootPath
import symplegades.core.transform.TransformAlg


trait ShowTransformAlg[Json] extends TransformAlg[Json, ShowPathElement, ShowFilter, ShowTransformResult] {
  type ShowPath = Path[ShowPathElement]
  type ShowNonRootPath = NonRootPath[ShowPathElement]

  implicit def jsonShow: Show[Json]

  val identity = (_: Json) => "Identity"
  def delete(path: ShowNonRootPath) = _ => s"Delete(${path.show})"
  def insert(path: ShowNonRootPath, toInsert: Json) = _ => s"Insert(${path.show}, ${toInsert.show})"
  def copy(from: ShowPath, to: ShowNonRootPath) = _ => s"Copy(${from.show}, ${to.show})"
  def move(from: ShowNonRootPath, to: ShowNonRootPath) = _ => s"Move(${from.show}, ${to.show})"
  def replaceValue(path: ShowPath, replacement: Json) = _ => s"ReplaceValue(${path.show}, ${replacement.show})"
  def focus(path: ShowPath, f: Trans) = json => s"Focus(${path.show}, ${f(json)}"
  def mapArray(path: ShowPath, f: ShowTransform[Json]) = json => s"MapArray(${path.show}, ${f(json)}"
  def conditional(filter: ShowFilter, trueTransform: Trans, falseTransform: Trans) = json => s"Conditional($filter, ${trueTransform(json)}, ${falseTransform(json)})"
  def composite(transforms: Trans*): Trans = json => s"Composite(${transforms.map(_(json)).mkString(", ")})"
}