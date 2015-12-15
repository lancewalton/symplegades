package symplegades.show

import symplegades.core.path.PathAlg

trait ShowPathAlg extends PathAlg[String] {
  def field(field: String) = field
}