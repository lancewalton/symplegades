package symplegades.transform

import symplegades.path.Path
import symplegades.path.NonRootPath

trait TransformAlg[PathElement, Transform, Json] {
	def noop(): Transform
  def delete(path: NonRootPath[PathElement]): Transform
  def insert(path: NonRootPath[PathElement], toInsert: Json): Transform
  def copy(from: Path[PathElement], to: NonRootPath[PathElement]): Transform
  def move(from: NonRootPath[PathElement], to: NonRootPath[PathElement]): Transform
  def replaceValue(path: Path[PathElement], replacement: Json): Transform
  def map(path: Path[PathElement], f: Transform): Transform
}