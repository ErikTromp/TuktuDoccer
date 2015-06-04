// Basic project information
name          := "TuktuDoccer"

version       := "1.0"

organization  := "tuktu"

scalaVersion  := "2.11.6"

// Add repositories
resolvers ++= Seq(
	"Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
    "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
)

// Add multiple dependencies
libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.4.0"
)