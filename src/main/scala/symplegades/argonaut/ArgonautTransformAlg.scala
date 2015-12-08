package symplegades.argonaut

import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.transform.TransformAlg
import symplegades.transform.TransformAlg
import symplegades.path.NonRootPath
import symplegades.path.RootPath
import scalaz.syntax.either._
import scalaz.syntax.std.option._
import scalaz.syntax.show._
import scalaz.syntax.std.list._
import scalaz.syntax.traverse._
import scalaz.\/
import scalaz.std.either._
import scalaz.syntax.applicative._
import scalaz.std.list._

object ArgonautTransformAlg extends TransformAlg[PathElement, Transform, Json] {
  type P = Path[PathElement]
  type NRP = NonRootPath[PathElement]

  def noop() = (json: Json) ⇒ json.right

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

  def insert(path: NRP, toInsert: Json): Transform = {
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
          case RootPath ⇒ workOnRoot(json).right[TransformFailure]
          case p: NRP   ⇒ workOnChild(json, p)
        }) { _ ⇒ TransformFailure(s"Insert: Could not insert ${toInsert}", json).left[Json] }
    }
  }

  def copy(from: P, to: NRP): Transform = (json: Json) ⇒ for {
    jsonToCopy ← composePath(from).get(json).orFail("Copy", "Could not copy", json)
    copied ← insert(to, jsonToCopy)(json)
  } yield copied

  def move(from: NRP, to: NRP): Transform = (json: Json) ⇒ for {
    copied ← copy(from, to)(json)
    deleted ← delete(from)(copied)
  } yield deleted

  def replaceValue(path: P, replacement: Json): Transform = (json: Json) ⇒
    composePath(path).set(json, replacement).orFail("ReplaceValue", s"Could not replace value: ${replacement.shows}", json)
    
  def map(path: P, f: Transform): Transform = (json: Json) =>
    for {
      jsonAtPath <- composePath(path).get(json).orFail("Map", "Path does not exist", json)
      arrayAtPath <- jsonAtPath.array.orFail("Map", "The element at the path is not an array", json)
      mappedArray <- arrayAtPath.map(f).sequenceU
      updatedJson <- composePath(path).set(json, jArray(mappedArray)).orFail("Map", s"Unable to set updated array: ${mappedArray.shows}", json)
    } yield updatedJson
}