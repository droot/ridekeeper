name := "ridekeeper"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.26",
  "org.specs2" %% "specs2" % "2.3.3",
  "org.mockito" % "mockito-all" % "1.9.5",
  "com.typesafe.play" %% "play-slick" % "0.5.0.8",
  "com.typesafe.slick" %% "slick" % "1.0.2",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.github.nscala-time" %% "nscala-time" % "0.6.0"
)

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)


resolvers += Resolver.file("My Repo", file(Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.ivyStylePatterns)

resolvers += "nexus-dev repository" at "http://nexus-dev/content/groups/public/"



play.Project.playScalaSettings
