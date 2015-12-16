package symplegades.core

import scalaz.\/

package object transform {
  type Transform[Json, TransformResult] = Json => TransformResult
}