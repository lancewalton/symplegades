package symplegades.core.filter

import symplegades.core.path.Path

trait FilterExtremeAlg[F] {
  def allPass(): F
  def noPass(): F
}

trait FilterLogicAlg[F, PathElement] {
  def and(lhs: F, rhs: F): F
  def or(lhs: F, rhs: F): F
  def not(filter: F): F
  def exists(path: Path[PathElement], filter: F): F
  def forall(path: Path[PathElement], filter: F): F
}

trait FilterNodeTypeAlg[F, PathElement] {
  def isObject(path: Path[PathElement]): F
  def isArray(path: Path[PathElement]): F
  def isNumber(path: Path[PathElement]): F
  def isString(path: Path[PathElement]): F
  def isBoolean(path: Path[PathElement]): F
  def isNull(path: Path[PathElement]): F
}

trait FilterNodeAlg[F, PathElement, Json] {
  def hasNode(path: Path[PathElement]): F
  def hasValue(path: Path[PathElement], value: Json): F
  def hasValueInSet(path: Path[PathElement], value: Json*): F
  def focusAndMatch(path: Path[PathElement], filter: F): F
}

trait FilterAllAlg[F, PathElement, Json] extends FilterExtremeAlg[F] with FilterLogicAlg[F, PathElement] with FilterNodeTypeAlg[F, PathElement] with FilterNodeAlg[F, PathElement, Json]
