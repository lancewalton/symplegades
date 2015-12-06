package symplegades.argonaut

import argonaut.Json
import symplegades.path.Path
import symplegades.transform.TransformAlg
import symplegades.transform.TransformAlg

object ArgonautTransformAlg extends TransformAlg[PathElement, Transformation] {
  def noop() = (json: Json) ⇒ Option(json)

  def delete(path: Path[PathElement]): Json ⇒ Option[Json] = {
    def workOnChild(json: Json, parentPath: Path[PathElement]) = for {
      parentJson ← composePath(parentPath).get(json)
      modifiedParentJson <- workOnRoot(parentJson)
      modifiedJson ← composePath(parentPath).set(json, modifiedParentJson)
    } yield modifiedJson
    
    def workOnRoot(json: Json) = for {
      fieldCursor ← json.cursor --\ path.lastElement.field
      deletedFieldCursor ← fieldCursor.delete
      modifiedJson = deletedFieldCursor.undo
    } yield modifiedJson

    (json: Json) ⇒ path.removeLastElement.fold(workOnRoot(json)) { workOnChild(json, _) }
  }
}