package symplegades.core.transform

case class TransformFailure[Json](msg: String, json: Json)
