package symplegades.argonaut

import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.transform.TransformAlg
import symplegades.transform.TransformAlg

object ArgonautTransformAlg extends TransformAlg[PathElement, Transform, Json] {
  def noop() = (json: Json) ⇒ Option(json)

  def delete(path: Path[PathElement]) = {
    def workOnRoot(json: Json) = for {
      fieldCursor ← json.cursor --\ path.lastElement.field
      deletedFieldCursor ← fieldCursor.delete
      modifiedJson = deletedFieldCursor.undo
    } yield modifiedJson

    def workOnChild(json: Json, parentPath: Path[PathElement]) = for {
      parentJson ← composePath(parentPath).get(json)
      modifiedParentJson ← workOnRoot(parentJson)
      modifiedJson ← composePath(parentPath).set(json, modifiedParentJson)
    } yield modifiedJson

    (json: Json) ⇒ path.removeLastElement.fold(workOnRoot(json)) { workOnChild(json, _) }
  }

  def insert(path: Path[PathElement], toInsert: Json): Transform = {
    def workOnRoot(json: Json) = (path.lastElement.field, toInsert) ->: json

    def workOnChild(json: Json, parentPath: Path[PathElement]): Option[Json] = {
      composePath(parentPath).get(json)
        .fold(insert(parentPath, (path.lastElement.field, toInsert) ->: jEmptyObject)(json)) { jsonAtParentPath ⇒ composePath(parentPath).set(json, (path.lastElement.field, toInsert) ->: jsonAtParentPath) }
    }

    (json: Json) ⇒
      composePath(path)
        .get(json)
        .swap(path.removeLastElement.fold(Option(workOnRoot(json))) { workOnChild(json, _) })
        .flatten
  }

  def copy(from: Path[PathElement], to: Path[PathElement]) = (json: Json) ⇒ for {
    jsonToCopy ← composePath(from).get(json)
    copied ← insert(to, jsonToCopy)(json)
  } yield copied

  def move(from: Path[PathElement], to: Path[PathElement]) = (json: Json) ⇒ for {
    copied ← copy(from, to)(json)
    deleted ← delete(from)(copied)
  } yield deleted
}