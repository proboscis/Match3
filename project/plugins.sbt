resolvers += "github repo" at "http://mpeltonen.github.com/maven"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"

resolvers += "spray" at "http://repo.spray.io/"

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.9")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")