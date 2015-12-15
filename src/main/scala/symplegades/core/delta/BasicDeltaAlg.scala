package symplegades.core.delta

trait BasicDeltaAlg[Filter, Transform] extends DeltaAlg[Filter, Transform, Delta[Filter, Transform]] {
  def simple(name: String, filter: Filter, transform: Transform): Delta[Filter, Transform] = SimpleDelta(name, filter, transform)
	def composite(name: String, filter: Filter, deltas: Delta[Filter, Transform]*): Delta[Filter, Transform] = CompositeDelta(name, filter, deltas: _*)
}