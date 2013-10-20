resolvers += "github repo" at "http://mpeltonen.github.com/maven"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

addSbtPlugin("com.github.mpeltonen" % "sbt-android-plugin" % "0.6.3-SNAPSHOT")

//addSbtPlugin("com.github.mpeltonen" % "sbt-android-plugin" % "0.7.1")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.3.0-SNAPSHOT")