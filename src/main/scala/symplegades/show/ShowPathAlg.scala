package symplegades.show

import symplegades.path.PathAlg

object ShowPathAlg extends PathAlg[String] {
  def element(field: String) = field
}