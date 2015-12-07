package symplegades.argonaut

import org.scalatest.{ Finders, FlatSpec, MustMatchers }

import argonaut.{ Cursor, Json, Parse }
import symplegades.filter.Filter
import symplegades.filter.FilterAlgSyntax.FilterAlgLogicSyntax
import symplegades.filter.{ FilterAllAlg, FilterAlgSyntax }
import symplegades.path.{ Path, PathAlg }

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

  type TypedFilterAlg = FilterAllAlg[Filter, PathElement, Json]
  type TypedPathAlg = PathAlg[PathElement]

  implicit val pathAlg = ArgonautPathAlg

  "delete" must "return None when the path does not exist" in {
    import Path._
    ArgonautTransformAlg.delete(path("z"))(rootJson) must be(None)
  }

  it must "return JSON that has the node removed when the node exists" in {
    import Path._
    val c = ArgonautTransformAlg.delete(path("y"))(rootJson)
    c must be(Some(
      parse("""|{
               |   "x": 1
               |}""")))
  }

  "insert" must "return None when the path already exists" in {
    import Path._
    val c = ArgonautTransformAlg.insert(path("x"), parse("3"))(rootJson)
    c must be(None)
  }

  it must "insert the required object when the insertion point is in the root object" in {
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
    import Path._
    val c = ArgonautTransformAlg.insert(root / "z" / "a", parse("3"))(rootJson)
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
    import Path._
    val c = ArgonautTransformAlg.insert(root / "y" / "a", parse("3"))(nestedJson)
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
    import Path._
    val c = ArgonautTransformAlg.insert(root / "y" / "a" / "b", parse("3"))(nestedJson)
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

  "copy" must "copy the specified node to the specified location" in {
    import Path._
    val c = ArgonautTransformAlg.copy(root / "y" / "z", path("z"))(nestedJson)
    c must be(Some(
      parse(
        """|{
           |  "x": 1,
           |  "y": {
           |    "z": 2
           |  },
           |  "z": 2
           |}""")))
  }

  "move" must "move the specified node to the specified location" in {
    import Path._
    val c = ArgonautTransformAlg.move(root / "y" / "z", root / "z" / "y")(nestedJson)
    c must be(Some(
      parse(
        """|{
           |  "x": 1,
           |  "y": {},
           |  "z": {
           |    "y": 2
           |  }
           |}""")))
  }

  "replace" must "replace the specified node with the new value" in {
    import Path._
    val c = ArgonautTransformAlg.replaceValue(root / "y" / "z", parse(""""Hello""""))(nestedJson)
    c must be(Some(
      parse(
        """|{
           |  "x": 1,
           |  "y": {
           |    "z": "Hello"
           |  }
           |}""")))
  }

  private def parse(json: String): Json =
    Parse.parse(json.stripMargin).fold(error ⇒ fail(s"Couldn't parse JSON: $error"), identity)
}
