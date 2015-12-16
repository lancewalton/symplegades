package symplegades

import symplegades.core.transform.Transform

package object show {
  type ShowPathElement = String
  type ShowFilter = String
  type ShowTransformResult = String
  type ShowTransform[Json] = Transform[Json, ShowTransformResult]
}