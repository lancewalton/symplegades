package symplegades.filter

object FilterAlgSyntax {
  implicit class FilterAlgLogicSyntax[F](filter: F)(implicit fla: FilterLogicAlg[F]) {
    import fla._
    def unary_! = not(filter)
    def &&(another: F) = and(filter, another)
    def ||(another: F) = or(filter, another)
  }
}