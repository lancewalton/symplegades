package symplegades.filter

import argonaut.Json

trait Filter extends Function[Json, Boolean]