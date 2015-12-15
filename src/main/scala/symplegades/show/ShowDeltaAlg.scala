package symplegades.show

import symplegades.core.delta.{ CompositeDelta, Delta, DeltaAlg, SimpleDelta }

trait ShowDeltaAlg extends DeltaAlg[String, String, String] {
  def simple(name: String, filter: String, transform: String) = s""""$name" when $filter { $transform }"""
	def composite(name: String, filter: String, deltas: String*)= s""""$name" when $filter { ${deltas.toList.mkString("and then")} }"""
}