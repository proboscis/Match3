resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies <+= sbtVersion ("org.scala-sbt" % "scripted-plugin" % _)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")
