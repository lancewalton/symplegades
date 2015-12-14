package symplegades.core.filter

trait Filter[Json] extends Function[Json, Boolean]