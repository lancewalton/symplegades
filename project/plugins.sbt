fullResolvers := (projectResolver.value +: Seq(
    Resolver.defaultLocal))

addSbtPlugin("com.gilt" % "sbt-dependency-graph-sugar" % "0.7.5-1")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")

