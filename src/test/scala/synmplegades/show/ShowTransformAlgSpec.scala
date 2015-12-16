package symplegades.show

import org.scalatest.{ Finders, FlatSpec, MustMatchers }
import argonaut.Json
import scalaz.Show
import scalaz.{ -\/, \/- }
import symplegades.core.path.Path
import symplegades.JsonUtils._

class ShowTransformAlgSpec extends FlatSpec with MustMatchers {
  val dummyJson = parse("true")

  implicit val pathAlg = new ShowPathAlg {}

  implicit val transformAlg = new ShowTransformAlg[Json] {
    val jsonShow = new Show[Json] {
      override def shows(json: Json) = json.toString
    }
  }

  import Path._

  "delete" must """return 'Delete' with the path""" in {
    transformAlg.delete(path("z"))(dummyJson) must be("""Delete("z")""")
  }

  "insert" must """return -\/ when the path already exists""" in {
    transformAlg.insert(path("x"), parse("3"))(dummyJson) must be("""Insert("x", 3)""")
  }

  "copy" must "copy the specified node to the specified location" in {
    transformAlg.copy(root / "y" / "z", path("z"))(dummyJson) must be("""Copy("y"/"z", "z")""")
  }

  "move" must "move the specified node to the specified location" in {
    transformAlg.move(root / "y" / "z", root / "z" / "y")(dummyJson) must be("""Move("y"/"z", "z"/"y")""")
  }

  "replaceValue" must "replace the specified node with the new value" in {
    transformAlg.replaceValue(root / "y" / "z", parse(""""Hello""""))(dummyJson) must be("""ReplaceValue("y"/"z", "Hello")""")
  }

  "map" must "replace the specified array with the mapped array" in {
    transformAlg.map(root / "y", transformAlg.replaceValue(root / "z", parse(""""Goodbye"""")))(dummyJson) must be("""Map("y", ReplaceValue("z", "Goodbye")""")
  }
}
