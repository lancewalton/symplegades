package symplegades.argonaut

import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.transform.TransformAlg
import symplegades.transform.TransformAlg
import symplegades.path.NonRootPath
import symplegades.path.RootPath

object ArgonautTransformAlg extends TransformAlg[PathElement, Transform, Json] {
  type P = Path[PathElement]
  type NRP = NonRootPath[PathElement]
  
  def noop() = (json: Json) ⇒ Option(json)

  def delete(path: NRP) = {
    def workOnRoot(json: Json) = for {
      fieldCursor ← json.cursor --\ path.lastElement.field
      deletedFieldCursor ← fieldCursor.delete
      modifiedJson = deletedFieldCursor.undo
    } yield modifiedJson

    def workOnChild(json: Json, parentPath: P) = for {
      parentJson ← composePath(parentPath).get(json)
      modifiedParentJson ← workOnRoot(parentJson)
      modifiedJson ← composePath(parentPath).set(json, modifiedParentJson)
    } yield modifiedJson

    (json: Json) ⇒ path.removeLastElement match {
      case RootPath => workOnRoot(json)
      case p: NRP => workOnChild(json, p)
    }
  }

  def insert(path: NRP, toInsert: Json): Transform = {
    def workOnRoot(json: Json): Json = (path.lastElement.field, toInsert) ->: json

    def workOnChild(json: Json, parentPath: NRP): Option[Json] = {
      composePath(parentPath).get(json)
        .fold(insert(parentPath, (path.lastElement.field, toInsert) ->: jEmptyObject)(json)) { jsonAtParentPath ⇒ composePath(parentPath).set(json, (path.lastElement.field, toInsert) ->: jsonAtParentPath) }
    }

    (json: Json) ⇒ {
      composePath(path)
        .get(json)
        .swap(path.removeLastElement match {
          case RootPath => Option(workOnRoot(json))
          case p: NRP => workOnChild(json, p)
        })
        .flatten
    }
  }

  def copy(from: P, to: NRP) = (json: Json) ⇒ for {
    jsonToCopy ← composePath(from).get(json)
    copied ← insert(to, jsonToCopy)(json)
  } yield copied

  def move(from: NRP, to: NRP) = (json: Json) ⇒ for {
    copied ← copy(from, to)(json)
    deleted ← delete(from)(copied)
  } yield deleted
  
  def replaceValue(path: P, replacement: Json) = (json: Json) => composePath(path).set(json, replacement)
}