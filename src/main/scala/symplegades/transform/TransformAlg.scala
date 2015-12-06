package symplegades.transform

import symplegades.path.Path

trait TransformAlg[PathElement, Transform, Json] {
	def noop(): Transform
  def delete(path: Path[PathElement]): Transform
  def insert(path: Path[PathElement], toInsert: Json): Transform
  def copy(from: Path[PathElement], to: Path[PathElement]): Transform
  def move(from: Path[PathElement], to: Path[PathElement]): Transform
}