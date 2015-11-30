package symplegades.filter

import org.scalatest.{ FlatSpec, MustMatchers }
import argonaut._
import Argonaut._
import symplegades.path.Path
import symplegades.path.PathAlg
import symplegades.path.ArgonautCursorPathAlg
import symplegades.value.FalseValue
import symplegades.value.TrueValue

class ArgonautCursorFilterAlgSpec extends FlatSpec with MustMatchers {
  type TypedFilterAlg = FilterAllAlg[Filter, Cursor ⇒ Option[Cursor]]
  type TypedPathAlg = PathAlg[Cursor ⇒ Option[Cursor]]

  "allPass" must "pass everything" in {
    val json = parse(
      """{
         | "x": 1
         |}""")

    ArgonautCursorFilterAlg.allPass(json.cursor) must be(true)
  }

  "noPass" must "pass nothing" in {
    val json = parse(
      """{
         | "x": 1
         |}""")

    ArgonautCursorFilterAlg.noPass(json.cursor) must be(false)
  }

  "hasNode" must "match when the JSON has the node" in {
    assertTrue {
      // Why can't I make the line below (implicit filterAlg, pathAlg) =>
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax

      hasNode(path("x") / "y")
    }("""|{
         |  "x": {
         |     "y": 1
         |  }
         |}""")
  }

  it must "not match when the JSON does not have the node" in {
    assertFalse {
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax

        hasNode(path("x") / "z")
    }(
      """|{
         |  "x": {
         |     "y": 1
         |  }
         |}""")
  }

  "hasValue" must "match when the JSON has the value" in {
    assertTrue {
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax

        hasValue(path("x") / "y", TrueValue)
    }("""|{
          |  "x": {
          |    "y": true
          |  }
          |}""")
  }

  it must "not match when the JSON does not have the value" in {
    assertFalse {
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax

        hasValue(path("x") / "y", FalseValue)
    }(
      """|{
         |  "x": {
         |     "y": true
         |  }
         |}""")
  }

  it must "not match when the JSON does not have the node" in {
    assertFalse {
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax

        hasValue(path("x") / "nope", FalseValue)
    }("""|{
         |   "x": {
         |     "y": true
         |   }
         |}""")
  }
  
  "focusAndMatch" must "not match when the focus path does not exist" in {
    assertFalse {
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        implicit val fa = filterAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax
        import FilterAlgSyntax._
        
        focusAndMatch(path("x"), hasNode(path("y")) && hasValue(path("z"), FalseValue))
    }("""|{
         |   "y": 1,
         |   "z": false
         |}""".stripMargin)    
  }
  
  it must "match when the JSON has the focus path and matches the filter" in {
    assertTrue {
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        implicit val fa = filterAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax
        import FilterAlgSyntax._
        
        focusAndMatch(path("x"), hasNode(path("y")) && hasValue(path("z"), FalseValue))
    }("""|{
         |   "x": {
         |     "y": 1,
         |     "z": false
         |   }
         |}""".stripMargin)
  }

  it must "not match when the JSON has the focus patch but doesn't match the filter" in {
    assertFalse {
      (filterAlg, pathAlg) ⇒
        implicit val pa = pathAlg
        implicit val fa = filterAlg
        import filterAlg._
        import pathAlg._
        import Path.PathSyntax
        import FilterAlgSyntax._
        
        focusAndMatch(path("x"), hasNode(path("y")) && hasValue(path("z"), FalseValue))
    }("""|{
         |   "x": {
         |     "y": 1,
         |     "z": true
         |   }
         |}""".stripMargin)
  }
  
  private def assertFalse(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter)(json: String) = verify(false, buildFilter, json)
  private def assertTrue(buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter)(json: String) = verify(true, buildFilter, json)

  private def verify(expected: Boolean, buildFilter: (TypedFilterAlg, TypedPathAlg) ⇒ Filter, json: String) =
    buildFilter(ArgonautCursorFilterAlg, ArgonautCursorPathAlg)(parse(json).cursor) must be(expected)

  private def parse(json: String): Json =
    Parse.parse(json.stripMargin).fold(error ⇒ fail(s"Couldn't parse JSON: $error"), identity)
}