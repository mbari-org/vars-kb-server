lazy val caffeineVersion   = "2.8.1"
lazy val catsVersion       = "2.1.1"
lazy val codecVersion      = "1.13"
lazy val configVersion     = "1.4.0"
lazy val derbyVersion      = "10.15.2.0"
lazy val fatboyVersion     = "1.1.1"
lazy val gsonVersion       = "2.8.6"
lazy val hikariVersion     = "3.4.2"
lazy val javamelodyVersion = "1.82.0"
lazy val jettyVersion      = "9.4.27.v20200227"
lazy val jtaVersion        = "1.1"
lazy val junitVersion      = "4.13"
lazy val logbackVersion    = "1.2.3"
lazy val oracleVersion     = "19.6.0.0"
lazy val scalatestVersion  = "3.0.8"
lazy val scalatraVersion   = "2.7.0"
lazy val servletVersion    = "4.0.1"
lazy val slf4jVersion      = "1.7.30"
lazy val sqlserverVersion  = "8.2.1.jre11"
lazy val varskbVersion     = "11.0.6.jre11"

lazy val buildSettings = Seq(
  organization := "org.mbari.vars",
  scalaVersion := "2.12.11",
  crossScalaVersions := Seq("2.12.11"),
  organizationName := "Monterey Bay Aquarium Research Institute",
  startYear := Some(2017),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
)

lazy val consoleSettings = Seq(
  shellPrompt := { state =>
    val user = System.getProperty("user.name")
    user + "@" + Project.extract(state).currentRef.project + ":sbt> "
  },
  initialCommands in console :=
    """
      |import java.time.Instant
      |import java.util.UUID
    """.stripMargin
)

lazy val dependencySettings = Seq(
  libraryDependencies ++= {
    Seq(
      "ch.qos.logback" % "logback-classic"  % logbackVersion,
      "ch.qos.logback" % "logback-core"     % logbackVersion,
      "com.typesafe"   % "config"           % configVersion,
      "junit"          % "junit"            % junitVersion % "test",
      "org.scalatest"  %% "scalatest"       % scalatestVersion % "test",
      "org.slf4j"      % "log4j-over-slf4j" % slf4jVersion,
      "org.slf4j"      % "slf4j-api"        % slf4jVersion
    )
  },
  resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("releases"),
    "hohonuuli-bintray" at "https://dl.bintray.com/hohonuuli/maven"
  )
)

lazy val optionSettings = Seq(
  scalacOptions ++= Seq(
    "-Ypartial-unification", // needed for cats
    "-deprecation",
    "-encoding",
    "UTF-8", // yes, this is 2 args
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    // "-Xfatal-warnings",
    "-Xlint"
  ),
  javacOptions ++= Seq("-target", "11", "-source", "11"),
  updateOptions := updateOptions.value.withCachedResolution(true)
)

lazy val varsSettings = buildSettings ++ consoleSettings ++ dependencySettings ++
  optionSettings // ++ reformatOnCompileSettings

lazy val apps = Map("jetty-main" -> "JettyMain") // for sbt-pack

lazy val root = (project in file("."))
  .enablePlugins(JettyPlugin)
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(PackPlugin)
  .settings(varsSettings)
  .settings(
    name := "vars-kb-server",
    version := "0.2.4",
    fork := true,
    libraryDependencies ++= {
      Seq(
        "com.microsoft.sqlserver"                        % "mssql-jdbc"                % "7.4.1.jre11",
        "com.fatboyindustrial.gson-javatime-serialisers" % "gson-javatime-serialisers" % fatboyVersion,
        "com.github.ben-manes.caffeine"                  % "caffeine"                  % caffeineVersion,
        "com.google.code.gson"                           % "gson"                      % gsonVersion,
        "com.microsoft.sqlserver"                        % "mssql-jdbc"                % sqlserverVersion,
        "com.oracle.database.jdbc"                       % "ojdbc10"                   % oracleVersion,
        "com.zaxxer"                                     % "HikariCP"                  % hikariVersion,
        "commons-codec"                                  % "commons-codec"             % codecVersion,
        "javax.servlet"                                  % "javax.servlet-api"         % servletVersion,
        "javax.transaction"                              % "jta"                       % jtaVersion,
        "org.apache.derby"                               % "derby"                     % derbyVersion, //          % "test",
        "org.apache.derby"                               % "derbyclient"               % derbyVersion, //          % "test",
        "org.apache.derby"                               % "derbynet"                  % derbyVersion, //          % "test",
        "org.apache.derby"                               % "derbyshared"               % derbyVersion,
        "org.apache.derby"                               % "derbytools"                % derbyVersion,
        "org.eclipse.jetty"                              % "jetty-server"              % jettyVersion % "compile;test",
        "org.eclipse.jetty"                              % "jetty-servlets"            % jettyVersion % "compile;test",
        "org.eclipse.jetty"                              % "jetty-webapp"              % jettyVersion % "compile;test",
        "org.mbari.m3"                                   % "org.mbari.kb.jpa"          % varskbVersion,
        "org.scalatest"                                  %% "scalatest"                % scalatestVersion % "test",
        "org.scalatra"                                   %% "scalatra"                 % scalatraVersion,
        "org.scalatra"                                   %% "scalatra-json"            % scalatraVersion,
        "org.scalatra"                                   %% "scalatra-scalate"         % scalatraVersion,
        "org.scalatra"                                   %% "scalatra-swagger"         % scalatraVersion,
        "org.scalatra"                                   %% "scalatra-scalatest"       % scalatraVersion,
        "org.typelevel"                                  %% "cats-core"                % catsVersion,
        "net.bull.javamelody"                            % "javamelody-core"           % javamelodyVersion
      ).map(
        _.excludeAll(
          ExclusionRule("org.slf4j", "slf4j-jdk14"),
          ExclusionRule("org.slf4j", "slf4j-log4j12"),
          ExclusionRule("javax.servlet", "servlet-api")
        )
      )
    }
  )
  .settings( // config sbt-pack
    packMain := apps,
    packExtraClasspath := apps.keys.map(k => k -> Seq("${PROG_HOME}/conf")).toMap,
    packJvmOpts := apps.keys.map(k => k        -> Seq("-Duser.timezone=UTC", "-Xmx4g")).toMap,
    packDuplicateJarStrategy := "latest",
    packJarNameConvention := "original"
  )

addCommandAlias("cleanall", ";clean;clean-files")
