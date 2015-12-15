package symplegades.argonaut

import org.scalatest.{ Finders, FlatSpec, MustMatchers }

import argonaut.{ Cursor, Json, Parse }
import symplegades.core.filter.{ Filter, FilterAllAlg, FilterAlgSyntax }
import symplegades.core.filter.FilterAlgSyntax.FilterAlgLogicSyntax
import symplegades.core.path.{ Path, PathAlg }
import scalaz.{ -\/, \/- }
import JsonUtils._

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

  type TypedFilterAlg = FilterAllAlg[JsonFilter, PathElement, Json]
  type TypedPathAlg = PathAlg[PathElement]

  implicit val pathAlg = new ArgonautPathAlg {}

  "delete" must """return -\/ when the path does not exist""" in {
    import Path._
    transformAlg.delete(path("z"))(rootJson) mustBe a[-\/[_]]
  }

  it must "return JSON that has the node removed when the node exists" in {
    import Path._
    val c = transformAlg.delete(path("y"))(rootJson)
    c must be(\/-(
      parse("""|{
               |   "x": 1
               |}""")))
  }

  "insert" must """return -\/ when the path already exists""" in {
    import Path._
    val c = transformAlg.insert(path("x"), parse("3"))(rootJson)
    c mustBe a[-\/[_]]
  }

  it must "insert the required object when the insertion point is in the root object" in {
    import Path._
    val c = transformAlg.insert(path("z"), parse("3"))(rootJson)
    c must be(\/-(
      parse("""|{
               |   "x": 1,
               |   "y": 2,
               |   "z": 3
               |}""")))
  }

  it must "insert the required object when intermediate objects need to be created from the root" in {
    import Path._
    val c = transformAlg.insert(root / "z" / "a", parse("3"))(rootJson)
    c must be(\/-(
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
    val c = transformAlg.insert(root / "y" / "a", parse("3"))(nestedJson)
    c must be(\/-(
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
    val c = transformAlg.insert(root / "y" / "a" / "b", parse("3"))(nestedJson)
    c must be(\/-(
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
    val c = transformAlg.copy(root / "y" / "z", path("z"))(nestedJson)
    c must be(\/-(
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
    val c = transformAlg.move(root / "y" / "z", root / "z" / "y")(nestedJson)
    c must be(\/-(
      parse(
        """|{
           |  "x": 1,
           |  "y": {},
           |  "z": {
           |    "y": 2
           |  }
           |}""")))
  }

  "replaceValue" must "replace the specified node with the new value" in {
    import Path._
    val c = transformAlg.replaceValue(root / "y" / "z", parse(""""Hello""""))(nestedJson)
    c must be(\/-(
      parse(
        """|{
           |  "x": 1,
           |  "y": {
           |    "z": "Hello"
           |  }
           |}""")))
  }

  "map" must "replace the specified array with the mapped array" in {
    import Path._

    val json = parse(
      """|{
       |  "x": 1,
       |  "y": [
       |    1,
       |    2
       |  ]
       |}""")

    val c = transformAlg.map(root / "y", j ⇒ j.number.flatMap { _.toInt.map(n ⇒ parse((n * 2).toString)) }.orFail("Test", "Can't", j))(json)
    c must be(\/-(
      parse(
        """|{
           |  "x": 1,
           |  "y": [
           |    2,
           |    4
           |  ]
           |}""")))
  }

  private def transformAlg = new ArgonautTransformAlg {}
}
