package symplegades.core

import scalaz.\/

package object transform {
  type Transform[Json] = Json => \/[TransformFailure[Json], Json]
}