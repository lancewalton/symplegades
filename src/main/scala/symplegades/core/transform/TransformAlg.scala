package symplegades.core.transform

import symplegades.core.path.Path
import symplegades.core.path.NonRootPath

trait TransformAlg[Json, PathElement, TransformResult] {
  type Trans = Json => TransformResult
  
	def noop(): Trans
  def delete(path: NonRootPath[PathElement]): Trans
  def insert(path: NonRootPath[PathElement], toInsert: Json): Trans
  def copy(from: Path[PathElement], to: NonRootPath[PathElement]): Trans
  def move(from: NonRootPath[PathElement], to: NonRootPath[PathElement]): Trans
  def replaceValue(path: Path[PathElement], replacement: Json): Trans
  def map(path: Path[PathElement], f: Trans): Trans
}