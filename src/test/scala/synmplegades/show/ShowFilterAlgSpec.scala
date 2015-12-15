package symplegades.show

import org.scalatest.{ Finders, FlatSpec, MustMatchers }
import _root_.argonaut.Json
import symplegades.core.filter.{ Filter, FilterAllAlg, FilterAlgSyntax }
import symplegades.core.filter.FilterAlgSyntax.FilterAlgLogicSyntax
import symplegades.core.path.{ Path, PathAlg }
import scalaz.Show
import symplegades.JsonUtils._

class ShowFilterAlgSpec extends FlatSpec with MustMatchers {
  type TypedFilterAlg = FilterAllAlg[String, String, String]
  type TypedPathAlg = PathAlg[String]

  implicit val filterAlg = new ShowFilterAlg[Json] {
    val jsonShow = new Show[Json] {
      override def shows(json: Json) = json.toString
    }
  }
  implicit val pathAlg = new ShowPathAlg {}
  
  "allPass" must "produce 'All'" in {
    filterAlg.allPass() must be("All")
  }

  "noPass" must "produce 'None'" in {
    filterAlg.noPass() must be("None")
  }

  "hasNode" must "produce 'HasNode' with the path" in {
    import pathAlg._
    import filterAlg._
    import Path._

    hasNode(path("x") / "y") must be("""HasNode("x"/"y")""")
  }
  
  "hasValue" must "produce 'HasValue' with the path and JSON" in {
    import pathAlg._
    import filterAlg._
    import Path._

    hasValue(path("x") / "y", parse("true")) must be("""HasValue("x"/"y", true)""")
  }

  "focusAndMatch" must "produce 'FocusAndMatch' with the path and filter" in {
    import pathAlg._
    import filterAlg._
    import Path._
    import FilterAlgSyntax._

    focusAndMatch("x", hasNode("y") && hasValue("z", parse("false"))) must be ("""FocusAndMatch("x", (HasNode("y") && HasValue("z", false)))""")
  }
      
  "isArray" must "produce 'IsArray' with the path" in {
    import pathAlg._
    import filterAlg._
    import Path._
    import FilterAlgSyntax._

    isArray("x") must be("""IsArray("x")""")
  }


  "exists" must "produce 'Exists' with the path and filter" in {
    import pathAlg._
    import filterAlg._
    import Path._
    import FilterAlgSyntax._

    exists("x", allPass) must be("""Exists("x", All)""")
  }
       
  "forAll" must "produce 'ForAll' with the path and filter" in {
    import pathAlg._
    import filterAlg._
    import Path._
    import FilterAlgSyntax._

    forall("x", allPass) must be("""ForAll("x", All)""")
  }
}
