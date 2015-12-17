package symplegades.show

import org.scalatest.{ Finders, FlatSpec, MustMatchers }
import argonaut.Json
import scalaz.Show
import scalaz.{ -\/, \/- }
import symplegades.core.path.Path
import symplegades.JsonUtils._
import SpecUtils._
import SpecUtils.filterAlg._
import SpecUtils.transformAlg._
import Path._

class ShowTransformAlgSpec extends FlatSpec with MustMatchers {
  "delete" must "return 'Delete' with the path" in {
    delete("z")(dummyJson) must be("""Delete("z")""")
  }

  "insert" must "return 'Insert' with the path and value" in {
    insert("x", parse("3"))(dummyJson) must be("""Insert("x", 3)""")
  }

  "copy" must "return 'Copy' with the from path and to path" in {
    copy(root / "y" / "z", "z")(dummyJson) must be("""Copy("y"/"z", "z")""")
  }

  "move" must "return 'Move' with the from path and to path" in {
    move(root / "y" / "z", root / "z" / "y")(dummyJson) must be("""Move("y"/"z", "z"/"y")""")
  }

  "replaceValue" must "return 'ReplaceValue' with the path and value" in {
    replaceValue(root / "y" / "z", parse(""""Hello""""))(dummyJson) must be("""ReplaceValue("y"/"z", "Hello")""")
  }

  "mapArray" must "return 'MapArray' with the path and value" in {
    mapArray("y", replaceValue("z", parse(""""Goodbye"""")))(dummyJson) must be("""MapArray("y", ReplaceValue("z", "Goodbye")""")
  }
  
  "conditional" must "return 'Conditional' with the true transform and false transform" in {
    conditional(hasNode("x"), insert(path("y") / "z", parse("true")), delete("x"))(dummyJson) must be("""Conditional(HasNode("x"), Insert("y"/"z", true), Delete("x"))""")
  }
  
  "composite" must "return 'Composite' with the sequence of transforms" in {
    composite(insert(path("y") / "z", parse("true")), delete("x"))(dummyJson) must be("""Composite(Insert("y"/"z", true), Delete("x"))""")
  }
}
