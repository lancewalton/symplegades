package symplegades.show

import symplegades.core.path.PathAlg

object ShowPathAlg extends PathAlg[String] {
  def element(field: String) = field
}