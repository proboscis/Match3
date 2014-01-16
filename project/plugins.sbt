resolvers += Resolver.url("scalasbt snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns)

resolvers += "github repo" at "http://mpeltonen.github.com/maven"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"

resolvers += "spray" at "http://repo.spray.io/"

addSbtPlugin("org.scala-sbt" % "sbt-android" % "0.7")

addSbtPlugin("com.hagerbot" % "sbt-robovm" % "0.1.0-SNAPSHOT")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0-SNAPSHOT")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.2")