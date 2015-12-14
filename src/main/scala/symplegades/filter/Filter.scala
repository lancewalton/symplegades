package symplegades.filter

trait Filter[Json] extends Function[Json, Boolean]