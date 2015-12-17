package symplegades.argonaut

import org.scalatest.{ Finders, FlatSpec, MustMatchers }
import argonaut.{ Cursor, Json, Parse }
import symplegades.core.filter.{ Filter, FilterAllAlg, FilterAlgSyntax }
import symplegades.core.filter.FilterAlgSyntax.FilterAlgLogicSyntax
import symplegades.core.path.{ Path, PathAlg }
import scalaz.{ -\/, \/- }
import symplegades.JsonUtils._

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

       
  private val filterAlg = new ArgonautFilterAlg {}
  import filterAlg._
  
  private val transformAlg = new ArgonautTransformAlg {}
  import transformAlg._
  
  implicit val pathAlg = new ArgonautPathAlg {}
  import Path._

  "delete" must """return -\/ when the path does not exist""" in {
    delete("z")(rootJson) mustBe a[-\/[_]]
  }

  it must "return JSON that has the node removed when the node exists" in {
    val c = delete("y")(rootJson)
    c must be(\/-(
      parse("""|{
               |   "x": 1
               |}""")))
  }

  "insert" must """return -\/ when the path already exists""" in {
    val c = insert("x", parse("3"))(rootJson)
    c mustBe a[-\/[_]]
  }

  it must "insert the required object when the insertion point is in the root object" in {
    val c = insert("z", parse("3"))(rootJson)
    c must be(\/-(
      parse("""|{
               |   "x": 1,
               |   "y": 2,
               |   "z": 3
               |}""")))
  }

  it must "insert the required object when intermediate objects need to be created from the root" in {
    val c = insert(root / "z" / "a", parse("3"))(rootJson)
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
    val c = insert(root / "y" / "a", parse("3"))(nestedJson)
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
    val c = insert(root / "y" / "a" / "b", parse("3"))(nestedJson)
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
    val c = copy(root / "y" / "z", "z")(nestedJson)
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
    val c = move(root / "y" / "z", root / "z" / "y")(nestedJson)
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
    val c = replaceValue(root / "y" / "z", parse(""""Hello""""))(nestedJson)
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
    val json = parse(
      """|{
       |  "x": 1,
       |  "y": [
       |    1,
       |    2
       |  ]
       |}""")

    val c = map("y", j ⇒ j.number.flatMap { _.toInt.map(n ⇒ parse((n * 2).toString)) }.orFail("Test", "Can't", j))(json)
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
  
  "conditional" must "return the true transform result if the condition returns true" in {
    val c = conditional(hasNode("x"), replaceValue("y", parse(""""correct"""")), replaceValue("y", parse(""""incorrect"""")))(rootJson)
    c must be(\/-(
      parse(
        """|{
           |  "x": 1,
           |  "y": "correct"
           |}""")))
  }
  
  it must "return the false transform result if the condition returns false" in {
    val c = conditional(hasNode("z"), replaceValue("y", parse(""""incorrect"""")), replaceValue("y", parse(""""correct"""")))(rootJson)
    c must be(\/-(
      parse(
        """|{
           |  "x": 1,
           |  "y": "correct"
           |}""")))
  }
    
  "composite" must "return the result of applying each of the transformations in sequence" in {
    val c = composite(replaceValue("y", parse(""""first"""")), replaceValue("x", parse(""""replaced"""")), replaceValue("y", parse(""""second"""")))(rootJson)
    c must be(\/-(
      parse(
        """|{
           |  "x": "replaced",
           |  "y": "second"
           |}""")))
  }
}
