import sbt._
object build extends Build{
	lazy val root = Project("plugins",file(".")) dependsOn(
		file("../android-sdk-plugin")
		)
}