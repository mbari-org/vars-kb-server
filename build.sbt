lazy val caffeineVersion   = "3.0.5"
lazy val catsVersion       = "2.7.0"
lazy val circeVersion      = "0.14.1"
lazy val codecVersion      = "1.15"
lazy val configVersion     = "1.4.2"
lazy val derbyVersion      = "10.15.2.0"
lazy val fatboyVersion     = "1.1.1"
lazy val gsonVersion       = "2.9.0"
lazy val guiceVersion      = "5.1.0"
lazy val hikariVersion     = "5.0.1"
lazy val httpcomponentsVersion = "4.5.13" // // override 4.5.6 in scalatratest. It's broken
lazy val jansiVersion      = "2.3.1"
lazy val javamelodyVersion = "1.90.0"
lazy val jettyVersion      = "9.4.45.v20220203"
lazy val jtaVersion        = "1.1"
lazy val junitVersion      = "4.13.2"
lazy val logbackVersion    = "1.3.0-alpha14"
lazy val oracleVersion     = "19.13.0.0.1"
lazy val postgresqlVersion   = "42.3.3"
lazy val scalatestVersion  = "3.2.11"
lazy val scalatraVersion   = "2.8.2"
lazy val servletVersion    = "3.1.0"
lazy val slf4jVersion      = "2.0.0-alpha6"
lazy val sqlserverVersion  = "9.4.1.jre11"
lazy val varskbVersion     = "11.0.12"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val buildSettings = Seq(
  organization := "org.mbari.vars",
  scalaVersion := "2.13.8",
  crossScalaVersions := Seq("2.13.8"),
  organizationName := "Monterey Bay Aquarium Research Institute",
  startYear := Some(2017),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
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
    Resolver.githubPackages("mbari-org")
//    "hohonuuli-bintray" at "https://dl.bintray.com/hohonuuli/maven"
  )
)

lazy val optionSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8", // yes, this is 2 args
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Xlint:-byname-implicit"// CirceCodecs fail to compile without this
  ),
  javacOptions ++= Seq("-target", "17", "-source", "17"),
  updateOptions := updateOptions.value.withCachedResolution(true)
)

lazy val varsSettings = buildSettings ++ dependencySettings ++
  optionSettings // ++ reformatOnCompileSettings

lazy val apps = Map("jetty-main" -> "JettyMain") // for sbt-pack

lazy val root = (project in file("."))
  .enablePlugins(
    AutomateHeaderPlugin, 
    GitBranchPrompt, 
    GitVersioning, 
    JettyPlugin,
    PackPlugin)
  .settings(varsSettings)
  .settings(
    name := "vars-kb-server",
    fork := true,
    // Set version based on git tag. I use "0.0.0" format (no leading "v", which is the default)
    // Use `show gitCurrentTags` in sbt to update/see the tags
    git.gitTagToVersionNumber := { tag: String =>
      if(tag matches "[0-9]+\\..*") Some(tag)
      else None
    },
    git.useGitDescribe := true,
    libraryDependencies ++= {
      Seq(
        "com.fatboyindustrial.gson-javatime-serialisers" % "gson-javatime-serialisers" % fatboyVersion,
        "com.github.ben-manes.caffeine"                  % "caffeine"                  % caffeineVersion,
        "com.google.code.gson"                           % "gson"                      % gsonVersion,
        "com.google.inject"                              % "guice"                     % guiceVersion,
        "com.microsoft.sqlserver"                        % "mssql-jdbc"                % sqlserverVersion,
        "com.oracle.database.jdbc"                       % "ojdbc10"                   % oracleVersion,
        "com.zaxxer"                                     % "HikariCP"                  % hikariVersion,
        "commons-codec"                                  % "commons-codec"             % codecVersion,
        "io.circe"                                       %% "circe-core"               % circeVersion,
        "io.circe"                                       %% "circe-generic"            % circeVersion,
        "io.circe"                                       %% "circe-parser"             % circeVersion,
        "javax.servlet"                                  % "javax.servlet-api"         % servletVersion,
        "javax.transaction"                              % "jta"                       % jtaVersion,
        "org.apache.derby"                               % "derby"                     % derbyVersion, //          % "test",
        "org.apache.derby"                               % "derbyclient"               % derbyVersion, //          % "test",
        "org.apache.derby"                               % "derbynet"                  % derbyVersion, //          % "test",
        "org.apache.derby"                               % "derbyshared"               % derbyVersion,
        "org.apache.derby"                               % "derbytools"                % derbyVersion,
        "org.apache.httpcomponents" % "httpcomponents-client"                % httpcomponentsVersion % Test,
        "org.apache.httpcomponents" % "httpmime"                  % httpcomponentsVersion % Test,
        "org.eclipse.jetty"                              % "jetty-server"              % jettyVersion % "compile;test",
        "org.eclipse.jetty"                              % "jetty-servlets"            % jettyVersion % "compile;test",
        "org.eclipse.jetty"                              % "jetty-webapp"              % jettyVersion % "compile;test",
        "org.eclipse.persistence" % "org.eclipse.persistence.jpa" % "2.7.10",
        "org.mbari.vars"                                 % "org.mbari.kb.jpa"          % varskbVersion,
        "org.postgresql"                                 % "postgresql"                % postgresqlVersion,
        "org.scalatest"                                  %% "scalatest"                % scalatestVersion % "test",
        "org.scalatra"                                   %% "scalatra"                 % scalatraVersion,
        "org.scalatra"                                   %% "scalatra-json"            % scalatraVersion,
        // "org.scalatra"                                   %% "scalatra-scalate"         % scalatraVersion,
//        "org.scalatra" %% "scalatra-slf4j" % scalatraVersion,
        // "org.scalatra" %% "scalatra-swagger" % scalatraVersion,
//        "org.scalatra" %% "scalatra-swagger-ext" % scalatraVersion,
        "org.scalatra"        %% "scalatra-scalatest" % scalatraVersion,
        "org.typelevel"       %% "cats-core"          % catsVersion,
        "net.bull.javamelody" % "javamelody-core"     % javamelodyVersion
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

