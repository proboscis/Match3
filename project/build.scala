import sbt._

import Keys._
import org.scalasbt.androidplugin._
import org.scalasbt.androidplugin.AndroidKeys._

object Settings {
  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.1",
    scalaVersion := "2.10.3-RC2",
    {
      //libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.1"
      //libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.3-RC2"
      libraryDependencies ++=  Seq(
        "org.scalaz" %% "scalaz-core" % "7.0.4",
        "org.scalaz" %% "scalaz-effect" % "7.0.4",
        "org.scalaz" %% "scalaz-typelevel" % "7.0.4",
        "org.scalacheck" %% "scalacheck" % "1.10.1" % "test")

      //libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.1"
    }
    ,
    updateLibgdxTask
   )

  lazy val desktop = Settings.common ++ Seq (
    fork in Compile := true
  )

  lazy val android = Settings.common ++
    AndroidProject.androidSettings ++
    AndroidMarketPublish.settings ++ Seq (
      platformName in Android := "android-10",
      keyalias in Android := "change-me",
      mainAssetsPath in Android := file("common/src/main/resources"),
      unmanagedBase <<= baseDirectory( _ /"src/main/libs" ),
      proguardOption in Android := 
      "-keep class com.badlogic.gdx.backends.android.** { *; }"+
      " -keep class scala.collection.SeqLike {public protected *;}"+
      "-keep class org.mozilla.** {*;}"+
      "-keep class com.esotericsoftware.** {*;}"+
      """
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# http://stackoverflow.com/questions/4525661/android-proguard-cant-find-dynamically-referenced-class-javax-swing
-dontwarn java.awt.**
-dontnote java.awt.**
-dontwarn com.badlogic.gdx.jnigen.**
-dontwarn com.badlogic.**
-dontnote com.badlogic.**

-libraryjars /libs/gdx-backend-android.jar
-libraryjars ../Pogopainter/libs/gdx.jar

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService
-keep class com.badlogic.**
-keep class com.badlogic.backends.**
-keep class * implements com.badlogic.gdx.utils.Json*
-keep class com.google.**
-keep class java.lang.reflect.**

# https://ofdev.zendesk.com/entries/20461397-android-pointless-proguard-cfg
-keep class com.openfeint** { <methods>; }

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}



-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.android.vending.billing.**

-keep enum * {    public static **[] values();    public static ** valueOf(java.lang.String); }

      """
    )

  val updateLibgdx = TaskKey[Unit]("update-gdx", "Updates libgdx")

  val updateLibgdxTask = updateLibgdx <<= streams map { (s: TaskStreams) =>
    import Process._
    import java.io._
    import java.net.URL
    import java.util.regex.Pattern
    
    // Declare names
    val baseUrl = "http://libgdx.badlogicgames.com/nightlies"
    val gdxName = "libgdx-nightly-latest"

    // Fetch the file.
    s.log.info("Pulling %s" format(gdxName))
    s.log.warn("This may take a few minutes...")
    val zipName = "%s.zip" format(gdxName)
    val zipFile = new java.io.File(zipName)
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    // Extract jars into their respective lib folders.
    s.log.info("Extracting common libs")
    val commonDest = file("common/lib")
    val commonFilter = new ExactFilter("gdx.jar")
    IO.unzip(zipFile, commonDest, commonFilter)

    s.log.info("Extracting desktop libs")
    val desktopDest = file("desktop/lib")
    val desktopFilter = new ExactFilter("gdx-natives.jar") |
    new ExactFilter("gdx-backend-lwjgl.jar") |
    new ExactFilter("gdx-backend-lwjgl-natives.jar")
    IO.unzip(zipFile, desktopDest, desktopFilter)

    s.log.info("Extracting ios libs")
    val iosDest = file("ios/libs")
    val iosFilter = GlobFilter("ios/*")
    IO.unzip(zipFile, iosDest, iosFilter)

    s.log.info("Extracting android libs")
    val androidDest = file("android/src/main/libs")
    val androidFilter = new ExactFilter("gdx-backend-android.jar") |
    new ExactFilter("armeabi/libgdx.so") |
    new ExactFilter("armeabi/libandroidgl20.so") |
    new ExactFilter("armeabi-v7a/libgdx.so") |
    new ExactFilter("armeabi-v7a/libandroidgl20.so")
    IO.unzip(zipFile, androidDest, androidFilter)

    // Destroy the file.
    zipFile.delete
    s.log.info("Update complete")
  }
}

object LibgdxBuild extends Build {
  val common = Project (
    "common",
    file("common"),
    settings = Settings.common
  )

  lazy val desktop = Project (
    "desktop",
    file("desktop"),
    settings = Settings.desktop
  ) dependsOn common

  lazy val android = Project (
    "android",
    file("android"),
    settings = Settings.android
  ) dependsOn common
}
