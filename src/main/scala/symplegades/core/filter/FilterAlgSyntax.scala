package symplegades.core.filter

object FilterAlgSyntax {
  implicit class FilterAlgLogicSyntax[F, PathElement](filter: F)(implicit fla: FilterLogicAlg[F, PathElement]) {
    import fla._
    def unary_! = not(filter)
    def &&(another: F) = and(filter, another)
    def ||(another: F) = or(filter, another)
  }
}