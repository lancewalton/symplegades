package symplegades.argonaut

import _root_.argonaut.Json
import org.scalatest.{FlatSpec, MustMatchers}
import symplegades.JsonUtils._
import symplegades.core.filter.{FilterAlgSyntax, FilterAllAlg}
import symplegades.core.path.{Path, PathAlg}
import Path._
  
class ArgonautPathAlgSpec extends FlatSpec with MustMatchers {
  type TypedPathAlg = PathAlg[PathElement]
  
  implicit val pathAlg: PathAlg[PathElement] = new ArgonautPathAlg {}

  "composePath" must "produce a lens that can retrieve a field that exists" in {
    val json = parse(
      """|{
         | "x": {
         |   "y": {
         |     "z" : 1
         |   }
         | }
         |}""")

    composePath(root / "x" / "y").get(json) must be(Some(parse("""{ "z" : 1 }""")))
  }
  
  it must "produce a lens that can set a field in the root of the object" in {
    val json = parse(
      """|{
         | "x": 1
         |}""")

    composePath(root / "x").set(json, parse("2")) must be(Some(parse("""{ "x": 2 }""")))
  }
  
  it must "produce a lens that can set a field in a non-root part of the object" in {
    val json = parse(
      """|{
         | "x": {
         |   "y": 1
         | }
         |}""")

    composePath(root / "x" / "y").set(json, parse("""{ "z": 2 }""")) must be(Some(parse(
      """|{
         | "x": {
         |   "y": {
         |     "z": 2
         |    }
         | }
         |}""")))
  }
}
