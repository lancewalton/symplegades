package symplegades

import _root_.argonaut.{Cursor, Json}
import symplegades.path.Path
import scalaz.@?>
import symplegades.path.RootPath
import symplegades.path.NonRootPath
import scalaz.PLensFamily
import scalaz.\/

package object argonaut {
  type PathType = Path[PathElement]
  type Transform = Json => \/[TransformFailure, Json]
  
  private[argonaut] def composePath(path: PathType): Json @?> Json = path match {
    case RootPath => PLensFamily.plensFamilyId
    case NonRootPath(p) => p.list.map(_.lens).reduceLeft(_ >=> _)
  }
}