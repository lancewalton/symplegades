package symplegades.core.path

trait PathAlg[E] {
  def element(field: String): E
}