resolvers += "github repo" at "http://mpeltonen.github.com/maven"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"

resolvers += "spray" at "http://repo.spray.io/"

addSbtPlugin("com.github.mpeltonen" % "sbt-android-plugin" % "0.6.3-SNAPSHOT")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.3.0-SNAPSHOT")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.2")