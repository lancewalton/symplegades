package symplegades.argonaut

import org.scalatest.{ Finders, FlatSpec, MustMatchers }

import argonaut.{ Cursor, Json, Parse }
import symplegades.filter.Filter
import symplegades.filter.FilterAlgSyntax.FilterAlgLogicSyntax
import symplegades.filter.{FilterAllAlg, FilterAlgSyntax}
import symplegades.path.Path.PathSyntax
import symplegades.path.{ Path, PathAlg }
import symplegades.value.{ FalseValue, TrueValue }

class ArgonautTransformAlgSpec extends FlatSpec with MustMatchers {
  type TypedFilterAlg = FilterAllAlg[Filter, CursorToOptionalCursor]
  type TypedPathAlg = PathAlg[CursorToOptionalCursor]

  "delete" must "return None when the path does not exist" in {
    val json = parse(
      """{
         | "x": 1
         |}""")

    import ArgonautPathAlg._
    ArgonautTransformAlg.delete(path("y"))(json.cursor) must be(None)
  }
  
  it must "return a cursor whose JSON has the node removed when the node exists" in {
    val json = parse(
      """{
         | "x": 1,
         | "y": 2
         |}""")
         
    import ArgonautPathAlg._
    val c = ArgonautTransformAlg.delete(path("y"))(json.cursor).map(_.undo)
    c must be(Some(
        parse("""|{
                 |   "x": 1
                 |}""")))
  }

  private def noMatch(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter)(json: String) = verify(false, buildFilter, json)
  private def hasMatch(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter)(json: String) = verify(true, buildFilter, json)

  private def verify(expected: Boolean, buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter, json: String) =
    buildFilter(ArgonautFilterAlg, ArgonautPathAlg)(parse(json).cursor) must be(expected)

  private def parse(json: String): Json =
    Parse.parse(json.stripMargin).fold(error ⇒ fail(s"Couldn't parse JSON: $error"), identity)
}