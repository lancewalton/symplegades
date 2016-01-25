package symplegades.core.filter

object FilterAlgSyntax {
  implicit class FilterAlgLogicSyntax[F, PathElement](filter: F)(implicit fla: FilterLogicAlg[F, PathElement]) {
    import fla._
    def unary_! : F = not(filter)
    def &&(another: F): F = and(filter, another)
    def ||(another: F): F = or(filter, another)
  }
}
