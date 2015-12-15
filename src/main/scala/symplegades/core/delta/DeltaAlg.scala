package symplegades.core.delta

trait DeltaAlg[Filter, Transform, D] {
  def simple(name: String, filter: Filter, transform: Transform): D
	def composite(name: String, filter: Filter, deltas: D*): D
}