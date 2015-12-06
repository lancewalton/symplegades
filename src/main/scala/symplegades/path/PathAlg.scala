package symplegades.path

trait PathAlg[E] {
  def /(field: String): E
}