package symplegades.filter

import symplegades.path.Path

object ShowFilterAlg extends FilterAllAlg[String, String] {
  def allPass() = "All"
  def noPass() = "None"
  def or(lhs: String, rhs: String) = s"($lhs OR $rhs)"
  def and(lhs: String, rhs: String) = s"($lhs AND $rhs)"
  def not(filter: String) = s"NOT($filter)"
  def hasNode(path: Path[String]) = s"HasNode(${showPath(path)})"
  def hasBooleanValue(path: Path[String], value: Boolean) = s"HasBooleanValue(${showPath(path)}, $value)"

  private def showPath(path: Path[String]) = path.path.list.mkString(".")
}