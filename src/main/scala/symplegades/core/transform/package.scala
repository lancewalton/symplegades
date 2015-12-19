package symplegades.core

package object transform {
  type Transform[Json, TransformResult] = Json => TransformResult
}