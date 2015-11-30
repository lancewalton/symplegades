package symplegades.path

trait PathAlg[E] {
  def path(field: String): Path[E]
  def /(path: Path[E], field: String): Path[E]
}