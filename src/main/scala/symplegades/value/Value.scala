package symplegades.value

import scalaz.Equal.equalA
import scalaz.Show

sealed trait Value
case object FalseValue extends Value
case object TrueValue extends Value
case class NumberValue(n: String) extends Value

object Value {
  implicit val show = new Show[Value] {
    override def shows(v: Value) = v match {
      case FalseValue => "false"
      case TrueValue => "true"
      case NumberValue(n) => n
    }
  }
  
  implicit val equal = equalA[Value]
}