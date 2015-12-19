package symplegades.argonaut

import argonaut.Argonaut.{ jArray, jEmptyObject }
import argonaut.Json
import CatJsonInstances.JsonInstances
import cats.data.Xor

import symplegades.core.path.{ Path, NonRootPath, RootPath }
import symplegades.core.transform.{ TransformAlg, TransformFailure }

import cats.syntax.xor._
import cats.syntax.show._
import cats.std.list._
import cats.syntax.traverse._

trait ArgonautTransformAlg extends TransformAlg[Json, PathElement, JsonFilter, JsonTransformResult] {
  type JsonPath = Path[PathElement]
  type JsonNonRootPath = NonRootPath[PathElement]

  def identity(): Json ⇒ Xor[Nothing, Json] = (json: Json) ⇒ json.right

  def delete(path: JsonNonRootPath): Json ⇒ Xor[JsonTransformFailure, Json] = {
    def workOnRoot(json: Json) = for {
      fieldCursor ← (json.cursor --\ path.lastElement.field).orFail("Delete", s"The field '${path.lastElement.field}' does not exist", json)
      deletedFieldCursor ← fieldCursor.delete.orFail("Delete", s"Could not delete field '${path.lastElement.field}'", json)
    } yield deletedFieldCursor.undo

    def workOnChild(json: Json, parentPath: JsonPath) = for {
      parentJson ← composePath(parentPath).getOption(json).orFail("Delete", s"Could not get the element at the specified path", json)
      modifiedParentJson ← workOnRoot(parentJson)
      modifiedJson ← composePath(parentPath).setOption(json)(modifiedParentJson).orFail("Delete", s"Could not set the element at the specified path", json)
    } yield modifiedJson

    (json: Json) ⇒ path.removeLastElement match {
      case RootPath           ⇒ workOnRoot(json)
      case p: JsonNonRootPath ⇒ workOnChild(json, p)
    }
  }

  def insert(path: JsonNonRootPath, toInsert: Json): JsonTransform = {
    def workOnRoot(json: Json): Json = (path.lastElement.field, toInsert) ->: json

    def workOnChild(json: Json, parentPath: JsonNonRootPath) = {
      composePath(parentPath).getOption(json)
        .fold(
          insert(parentPath, (path.lastElement.field, toInsert) ->: jEmptyObject)(json)) { jsonAtParentPath ⇒
            composePath(parentPath).setOption(json)( (path.lastElement.field, toInsert) ->: jsonAtParentPath).orFail("Insert", "Could not insert", json)
          }
    }

    (json: Json) ⇒ {
      composePath(path)
        .getOption(json)
        .fold(path.removeLastElement match {
          case RootPath           ⇒ workOnRoot(json).right[TransformFailure[Json]]
          case p: JsonNonRootPath ⇒ workOnChild(json, p)
        }) { _ ⇒ TransformFailure(s"Insert: Could not insert $toInsert", json).left[Json] }
    }
  }

  def copy(from: JsonPath, to: JsonNonRootPath): JsonTransform = (json: Json) ⇒ for {
    jsonToCopy ← composePath(from).getOption(json).orFail("Copy", "Could not copy", json)
    copied ← insert(to, jsonToCopy)(json)
  } yield copied

  def move(from: JsonNonRootPath, to: JsonNonRootPath): JsonTransform = (json: Json) ⇒ for {
    copied ← copy(from, to)(json)
    deleted ← delete(from)(copied)
  } yield deleted

  def replaceValue(path: JsonPath, replacement: Json): JsonTransform = (json: Json) ⇒
    composePath(path).setOption(json)(replacement).orFail("ReplaceValue", s"Could not replace value: ${replacement.show}", json)

  def focus(path: JsonPath, f: JsonTransform): JsonTransform = (json: Json) =>
    for {
      jsonAtPath <- composePath(path).getOption(json).orFail("Focus", "Path does not exist", json)
      mappedValue <- f(jsonAtPath)
      updatedJson <- composePath(path).setOption(json)(mappedValue).orFail("Focus", s"Unable to set updated value: ${mappedValue.show}", json)
    } yield updatedJson

  def mapArray(path: JsonPath, f: JsonTransform): JsonTransform = (json: Json) ⇒
    for {
      jsonAtPath ← composePath(path).getOption(json).orFail("MapArray", "Path does not exist", json)
      arrayAtPath ← jsonAtPath.array.orFail("MapArray", "The element at the path is not an array", json)
      mappedArray ← arrayAtPath.map(f).sequenceU
      updatedJson ← composePath(path).setOption(json)(jArray(mappedArray)).orFail("MapArray", s"Unable to set updated array: ${mappedArray.show}", json)
    } yield updatedJson

  def conditional(filter: JsonFilter, trueTransform: JsonTransform, falseTransform: JsonTransform): JsonTransform = (json: Json) =>
    if (filter(json)) trueTransform(json) else falseTransform(json)

  def composite(transforms: JsonTransform*): JsonTransform = (json: Json) =>
    transforms.foldLeft(json.right[JsonTransformFailure]) { _ flatMap _ }
}