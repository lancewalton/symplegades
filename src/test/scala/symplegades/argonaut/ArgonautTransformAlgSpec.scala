package symplegades.argonaut

import org.scalatest.{ Finders, FlatSpec, MustMatchers }

import argonaut.{ Cursor, Json, Parse }
import symplegades.filter.Filter
import symplegades.filter.FilterAlgSyntax.FilterAlgLogicSyntax
import symplegades.filter.{ FilterAllAlg, FilterAlgSyntax }
import symplegades.path.{ Path, PathAlg }
import symplegades.value.{ FalseValue, TrueValue }

class ArgonautTransformAlgSpec extends FlatSpec with MustMatchers {
  val rootJson = parse(
    """|{
       |  "x": 1,
       |  "y": 2
       |}""")

  val nestedJson = parse(
    """|{
       |  "x": 1,
       |  "y": {
       |    "z": 2
       |  }
       |}""")

  type TypedFilterAlg = FilterAllAlg[Filter, PathElement]
  type TypedPathAlg = PathAlg[PathElement]
  
  implicit val pathAlg = ArgonautPathAlg

  "delete" must "return None when the path does not exist" in {
    import ArgonautPathAlg._
    import Path._
    ArgonautTransformAlg.delete(path("z"))(rootJson) must be(None)
  }

  it must "return JSON that has the node removed when the node exists" in {
    import ArgonautPathAlg._
    import Path._
    val c = ArgonautTransformAlg.delete(path("y"))(rootJson)
    c must be(Some(
      parse("""|{
               |   "x": 1
               |}""")))
  }

  "insert" must "return None when the path already exists" in {
    import ArgonautPathAlg._
    import Path._
    val c = ArgonautTransformAlg.insert(path("x"), parse("3"))(rootJson)
    c must be(None)
  }

  it must "insert the required object when the insertion point is in the root object" in {
    import ArgonautPathAlg._
    import Path._
    val c = ArgonautTransformAlg.insert(path("z"), parse("3"))(rootJson)
    c must be(Some(
      parse("""|{
               |   "x": 1,
    		       |   "y": 2,
    		       |   "z": 3
               |}""")))
  }
  
  it must "insert the required object when intermediate objects need to be created from the root" in {
    import ArgonautPathAlg._
    import Path._
    val c = ArgonautTransformAlg.insert(path("z") / "a", parse("3"))(rootJson)
    c must be(Some(
      parse("""|{
               |   "x": 1,
               |   "y": 2,
               |   "z": {
               |     "a": 3
               |   }
               |}""")))
  }

  it must "insert the required object when the insertion point is in a child object" in {
    import ArgonautPathAlg._
    import Path._
    val c = ArgonautTransformAlg.insert(path("y") / "a", parse("3"))(nestedJson)
    c must be(Some(
      parse(
        """|{
           |  "x": 1,
           |  "y": {
           |    "z": 2,
           |    "a": 3
           |  }
           |}""")))
  }
  
  it must "insert the required object when intermediate objects need to be created from a child object" in {
    import ArgonautPathAlg._
    import Path._
    val c = ArgonautTransformAlg.insert(path("y") / "a" / "b", parse("3"))(nestedJson)
    c must be(Some(
      parse(
        """|{
           |  "x": 1,
           |  "y": {
           |    "z": 2,
           |    "a": {
           |      "b": 3
           |    }
           |  }
           |}""")))
  }

  private def noMatch(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter)(json: String) = verify(false, buildFilter, json)
  private def hasMatch(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter)(json: String) = verify(true, buildFilter, json)

  private def verify(expected: Boolean, buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter, json: String) =
    buildFilter(ArgonautFilterAlg, ArgonautPathAlg)(parse(json)) must be(expected)

  private def parse(json: String): Json =
    Parse.parse(json.stripMargin).fold(error ⇒ fail(s"Couldn't parse JSON: $error"), identity)
}