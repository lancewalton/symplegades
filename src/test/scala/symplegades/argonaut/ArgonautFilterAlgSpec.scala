package symplegades.argonaut

import _root_.argonaut.Json
import org.scalatest.{FlatSpec, MustMatchers}
import symplegades.JsonUtils._
import symplegades.core.filter.{FilterAlgSyntax, FilterAllAlg}
import symplegades.core.path.{Path, PathAlg}

class ArgonautFilterAlgSpec extends FlatSpec with MustMatchers {
  type TypedFilterAlg = FilterAllAlg[JsonFilter, PathElement, Json]
  type TypedPathAlg = PathAlg[PathElement]

  "allPass" must "pass everything" in {
    val json = parse(
      """{
         | "x": 1
         |}""")

    filterAlg.allPass(json) must be(true)
  }

  "noPass" must "pass nothing" in {
    val json = parse(
      """{
         | "x": 1
         |}""")

    filterAlg.noPass(json) must be(false)
  }

  "hasNode" must "match when the JSON has the node" in hasMatch {
    // Why can't I make the line below (implicit filterAlg, pathAlg) =>
    (filterAlg, pathAlg) ⇒
      implicit val pa = pathAlg
      import Path._
      import filterAlg._

      hasNode(path("x") / "y")
  }("""|{
       |  "x": {
       |     "y": 1
       |  }
       |}""")

  it must "not match when the JSON does not have the node" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    import Path._
    import filterAlg._

    hasNode(path("x") / "z")
  }(
    """|{
       |  "x": {
       |     "y": 1
       |  }
       |}""")

  "hasValue" must "match when the JSON has the value" in hasMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    import Path._
    import filterAlg._

    hasValue(path("x") / "y", parse("true"))
  }("""|{
        |  "x": {
        |    "y": true
        |  }
        |}""")

  it must "not match when the JSON does not have the value" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    import Path._
    import filterAlg._

    hasValue(path("x") / "y", parse("false"))
  }(
    """|{
       |  "x": {
       |     "y": true
       |  }
       |}""")

  it must "not match when the JSON does not have the node" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    import Path._
    import filterAlg._

    hasValue(path("x") / "nope", parse("false"))
  }("""|{
       |   "x": {
       |     "y": true
       |   }
       |}""")

  "focusAndMatch" must "not match when the focus path does not exist" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import FilterAlgSyntax._
    import Path._
    import filterAlg._

    focusAndMatch("x", hasNode(path("y")) && hasValue("z", parse("false")))
  }("""|{
       |   "y": 1,
       |   "z": false
       |}""".stripMargin)

  it must "match when the JSON has the focus path and matches the filter" in hasMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import FilterAlgSyntax._
    import Path._
    import filterAlg._

    focusAndMatch("x", hasNode("y") && hasValue("z", parse("false")))
  }("""|{
       |   "x": {
       |     "y": 1,
       |     "z": false
       |   }
       |}""".stripMargin)

  it must "not match when the JSON has the focus path but doesn't match the filter" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import FilterAlgSyntax._
    import Path._
    import filterAlg._

    focusAndMatch("x", hasNode("y") && hasValue("z", parse("false")))
  }("""|{
       |   "x": {
       |     "y": 1,
       |     "z": true
       |   }
       |}""".stripMargin)

  "isArray" must "not match when the JSON does not have the path" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    isArray("x")
  }("""|{
       |   "y": 1
       |}""".stripMargin)

  it must "not match when the JSON has the path but the element is not an array" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    isArray("x")
  }("""|{
		  |   "x": 1
		  |}""".stripMargin)
      
  it must "match when the JSON has the path and the element is an array" in hasMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    isArray("x")
  }("""|{
      |   "x": []
      |}""".stripMargin)

  "exists" must "not match when the JSON has the path but is not an array" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    exists("x", allPass)
  }("""|{
       |   "x": "Not an array"
       |}""".stripMargin)

  it must "match when the JSON has an array at the path and the predicate is true for at least one of the elements" in hasMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    exists("x", hasValue(root[PathElement], parse("1")))
  }("""|{
       |   "x": [
       |     1, 2
       |   ]
       |}""".stripMargin)


  it must "not match when the JSON has an array at the path and the predicate is not true for any of the elements" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    exists("x", hasValue(root[PathElement], parse("2")))
  }("""|{
       |   "x": [
       |     1, 3
       |   ]
       |}""".stripMargin)
       
  "forAll" must "not match when the JSON has the path but is not an array" in noMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    forall("x", allPass)
  }("""|{
       |   "x": "Not an array"
       |}""".stripMargin)

  it must "match when the JSON has an array at the path and the predicate is true for all of the elements" in hasMatch { (filterAlg, pathAlg) ⇒
    implicit val pa = pathAlg
    implicit val fa = filterAlg
    import Path._
    import filterAlg._

    forall("x", hasValue(root[PathElement], parse("1")))
  }("""|{
       |   "x": [
       |     1, 1
       |   ]
       |}""".stripMargin)
       
  it must "not match when the JSON has an array at the path but the predicate is not true for all of the elements" in noMatch { (filterAlg, pathAlg) ⇒
	  implicit val pa = pathAlg
	  implicit val fa = filterAlg
	  import Path._
	  import filterAlg._
	  
	  forall("x", hasValue(root[PathElement], parse("1")))
  }("""|{
		  |   "x": [
		  |     1, 2
		  |   ]
		  |}""".stripMargin)
       
  private def noMatch(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ JsonFilter)(json: String) = verify(false, buildFilter, json)
  private def hasMatch(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ JsonFilter)(json: String) = verify(true, buildFilter, json)

  private def verify(expected: Boolean, buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ JsonFilter, json: String) =
    buildFilter(filterAlg, pathAlg)(parse(json)) must be(expected)

  private def filterAlg = new ArgonautFilterAlg {}
  private def pathAlg = new ArgonautPathAlg {}
}
