resolvers += "github repo" at "http://mpeltonen.github.com/maven"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"

addSbtPlugin("com.github.mpeltonen" % "sbt-android-plugin" % "0.6.3-SNAPSHOT")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.3.0-SNAPSHOT")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.2")

addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.1.1")