lazy val akkaHttpVersion = "10.7.0"
lazy val akkaVersion    = "2.10.3"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.13.16"
    )),
    name := "My Akka HTTP Project",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka" %% "akka-pki"                 % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"               % akkaVersion,
      "com.github.jwt-scala" %% "jwt-spray-json"        % "8.0.1",
      "ch.qos.logback"    % "logback-classic"           % "1.4.11",
      "org.slf4j"         % "slf4j-api"                 % "2.0.5"
    )
  )