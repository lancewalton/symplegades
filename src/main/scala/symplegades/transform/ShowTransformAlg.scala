package symplegades.transform

import symplegades.path.Path
import scalaz.syntax.show._
import scalaz.std.string._

object ShowTransformAlg extends TransformAlg[String, String] {
  val noop = "Noop"
  def delete(path: Path[String]) = s"Delete(${path.shows})"
}