package symplegades.filter

import argonaut.Cursor

trait Filter extends Function[Cursor, Boolean]