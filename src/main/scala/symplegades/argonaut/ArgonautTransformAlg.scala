package symplegades.argonaut

import argonaut.Json
import symplegades.path.Path
import symplegades.transform.TransformAlg
import symplegades.transform.TransformAlg

object ArgonautTransformAlg extends TransformAlg[PathElement, Transformation] {
	def noop() = (json: Json) => Option(json)
  
  def delete(path: Path[PathElement]) = (json: Json) => composePath(path).get(json)
}