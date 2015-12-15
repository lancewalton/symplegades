package symplegades.core.delta

import scala.language.implicitConversions

trait Delta[Filter, Transform] extends Product with Serializable {
}
case class SimpleDelta[Filter, Transform](name: String, filter: Filter, transform: Transform) extends Delta[Filter, Transform]
case class CompositeDelta[Filter, Transform](name: String, filter: Filter, deltas: Delta[Filter, Transform]*) extends Delta[Filter, Transform]

object Delta {
  implicit class DeltaCreationSyntax[Filter, Transform, D](name: String)(implicit da: DeltaAlg[Filter, Transform, D]) {
    trait Deltas {
      def change(transform: Transform): D
      def changes(deltas: D*): D
    }
    def when(filter: Filter) = new Deltas {
      def change(transform: Transform): D = da.simple(name, filter, transform)
      def changes(deltas: D*): D = da.composite(name, filter, deltas: _*)
    }
  }
}