package symplegades.transform

import symplegades.path.Path

trait TransformAlg[P, T] {
	def noop(): T
  def delete(path: Path[P]): T
}