package symplegades.path

object ShowPathAlg extends PathAlg[String] {
  def path(field: String) = Path.asPath(field)
  def /(path: Path[String], field: String) = path andThen field
}