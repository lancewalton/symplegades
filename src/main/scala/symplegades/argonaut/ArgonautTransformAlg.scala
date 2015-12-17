package symplegades.argonaut

import argonaut.Argonaut.{ JsonInstances, jArray, jEmptyObject }
import argonaut.Json
import scalaz.std.list._
import scalaz.syntax.either.ToEitherOps
import scalaz.syntax.show.ToShowOps
import scalaz.syntax.traverse.ToTraverseOps
import symplegades.core.path.{ NonRootPath, Path, RootPath }
import symplegades.core.transform.{ TransformAlg, TransformFailure }

trait ArgonautTransformAlg extends TransformAlg[Json, PathElement, JsonFilter, JsonTransformResult] {
  type P = Path[PathElement]
  type NRP = NonRootPath[PathElement]

  def identity() = (json: Json) ⇒ json.right

  def delete(path: NRP) = {
    def workOnRoot(json: Json) = for {
      fieldCursor ← (json.cursor --\ path.lastElement.field).orFail("Delete", s"The field '${path.lastElement.field}' does not exist", json)
      deletedFieldCursor ← fieldCursor.delete.orFail("Delete", s"Could not delete field '${path.lastElement.field}'", json)
    } yield deletedFieldCursor.undo

    def workOnChild(json: Json, parentPath: P) = for {
      parentJson ← composePath(parentPath).get(json).orFail("Delete", s"Could not get the element at the specified path", json)
      modifiedParentJson ← workOnRoot(parentJson)
      modifiedJson ← composePath(parentPath).set(json, modifiedParentJson).orFail("Delete", s"Could not set the element at the specified path", json)
    } yield modifiedJson

    (json: Json) ⇒ path.removeLastElement match {
      case RootPath ⇒ workOnRoot(json)
      case p: NRP   ⇒ workOnChild(json, p)
    }
  }

  def insert(path: NRP, toInsert: Json): JsonTransform = {
    def workOnRoot(json: Json): Json = (path.lastElement.field, toInsert) ->: json

    def workOnChild(json: Json, parentPath: NRP) = {
      composePath(parentPath).get(json)
        .fold(
          insert(parentPath, (path.lastElement.field, toInsert) ->: jEmptyObject)(json)) { jsonAtParentPath ⇒
            composePath(parentPath).set(json, (path.lastElement.field, toInsert) ->: jsonAtParentPath)
              .orFail("Insert", "Could not insert", json)
          }
    }

    (json: Json) ⇒ {
      composePath(path)
        .get(json)
        .fold(path.removeLastElement match {
          case RootPath ⇒ workOnRoot(json).right[TransformFailure[Json]]
          case p: NRP   ⇒ workOnChild(json, p)
        }) { _ ⇒ TransformFailure(s"Insert: Could not insert ${toInsert}", json).left[Json] }
    }
  }

  def copy(from: P, to: NRP): JsonTransform = (json: Json) ⇒ for {
    jsonToCopy ← composePath(from).get(json).orFail("Copy", "Could not copy", json)
    copied ← insert(to, jsonToCopy)(json)
  } yield copied

  def move(from: NRP, to: NRP): JsonTransform = (json: Json) ⇒ for {
    copied ← copy(from, to)(json)
    deleted ← delete(from)(copied)
  } yield deleted

  def replaceValue(path: P, replacement: Json): JsonTransform = (json: Json) ⇒
    composePath(path).set(json, replacement).orFail("ReplaceValue", s"Could not replace value: ${replacement.shows}", json)

  def map(path: P, f: JsonTransform): JsonTransform = (json: Json) ⇒
    for {
      jsonAtPath ← composePath(path).get(json).orFail("Map", "Path does not exist", json)
      arrayAtPath ← jsonAtPath.array.orFail("Map", "The element at the path is not an array", json)
      mappedArray ← arrayAtPath.map(f).sequenceU
      updatedJson ← composePath(path).set(json, jArray(mappedArray)).orFail("Map", s"Unable to set updated array: ${mappedArray.shows}", json)
    } yield updatedJson
    
  def conditional(filter: JsonFilter, trueTransform: JsonTransform, falseTransform: JsonTransform): JsonTransform = (json: Json) =>
    if (filter(json)) trueTransform(json) else falseTransform(json)
    
  def composite(transforms: JsonTransform*): JsonTransform = (json: Json) =>
    transforms.foldLeft(json.right[JsonTransformFailure]) { _ flatMap _}
}