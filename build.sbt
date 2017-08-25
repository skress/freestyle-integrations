import sbtorgpolicies.runnable.syntax._

lazy val fsVersion = Option(sys.props("frees.version")).getOrElse("0.3.2-SNAPSHOT")

lazy val fCoreDeps = "io.frees" %% "frees-core" % fsVersion :: Nil //freestyleCoreDeps(Some(fsVersion))

lazy val root = (project in file("."))
  .settings(moduleName := "root")
  .settings(name := "freestyle-integrations")
  .settings(noPublishSettings: _*)
  .aggregate(allModules: _*)

lazy val monix = module("monix", full = false)
  .jsSettings(sharedJsSettings: _*)
  .crossDepSettings(commonDeps ++ fCoreDeps ++
    Seq(%("monix-eval"), %("monix-cats")): _*)

lazy val monixJVM = monix.jvm
lazy val monixJS  = monix.js

lazy val cacheRedis = jvmModule("cache-redis")
  .settings(
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      %%("rediscala"),
      "io.frees" %% "frees-cache" % fsVersion,
      %%("akka-actor")    % "test",
      %("embedded-redis") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val doobie = jvmModule("doobie")
  .settings(
    libraryDependencies ++= Seq(
      %%("doobie-core-cats"),
      %%("doobie-h2-cats") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val slick = jvmModule("slick")
  .settings(
    libraryDependencies ++= Seq(
      %%("slick"),
      "io.frees" %% "frees-async" % fsVersion,
      %("h2") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val twitterUtil = jvmModule("twitter-util")
  .settings(
    libraryDependencies ++= Seq(%%("catbird-util")) ++ commonDeps ++ fCoreDeps
  )

lazy val fetch = module("fetch")
  .jsSettings(sharedJsSettings: _*)
  .crossDepSettings(
    commonDeps ++ fCoreDeps ++ Seq(
      %("fetch"),
      %("fetch-monix")
    ): _*
  )

lazy val fetchJVM = fetch.jvm
lazy val fetchJS  = fetch.js

lazy val fs2 = module("fs2")
  .jsSettings(sharedJsSettings: _*)
  .crossDepSettings(
    commonDeps ++ fCoreDeps ++ Seq(
      %("fs2-core")
    ): _*
  )

lazy val fs2JVM = fs2.jvm
lazy val fs2JS  = fs2.js

lazy val httpHttp4s = (project in file("modules/http/http4s"))
  .settings(name := "frees-http-http4s")
  .settings(
    libraryDependencies ++= Seq(
      %%("http4s-core"),
      %%("http4s-dsl") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val httpFinch = (project in file("modules/http/finch"))
  .settings(name := "frees-http-finch")
  .settings(
    libraryDependencies ++= Seq(%%("finch-core", "0.14.1")) ++ commonDeps ++ fCoreDeps
  )

lazy val httpAkka = (project in file("modules/http/akka"))
  .settings(name := "frees-http-akka")
  .settings(
    libraryDependencies ++= Seq(
      %%("akka-http"),
      %%("akka-http-testkit") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val httpPlay = (project in file("modules/http/play"))
  .disablePlugins(CoursierPlugin)
  .settings(name := "frees-http-play")
  .settings(
    concurrentRestrictions in Global := Seq(Tags.limitAll(1)),
    libraryDependencies ++= Seq(
      %%("play")      % "test",
      %%("play-test") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

pgpPassphrase := Some(getEnvVar("PGP_PASSPHRASE").getOrElse("").toCharArray)
pgpPublicRing := file(s"$gpgFolder/pubring.gpg")
pgpSecretRing := file(s"$gpgFolder/secring.gpg")

lazy val jvmModules: Seq[ProjectReference] = Seq(
  monixJVM,
  cacheRedis,
  doobie,
  slick,
  twitterUtil,
  fetchJVM,
  fs2JVM,
  httpHttp4s,
  httpFinch,
  httpAkka,
  httpPlay
)

lazy val jsModules: Seq[ProjectReference] = Seq(
  monixJS,
  fetchJS,
  fs2JS
)

lazy val allModules: Seq[ProjectReference] = jvmModules ++ jsModules

addCommandAlias("validateJVM", (toCompileTestList(jvmModules) ++ List("project root")).asCmd)
addCommandAlias("validateJS", (toCompileTestList(jsModules) ++ List("project root")).asCmd)
addCommandAlias(
  "validate",
  ";clean;compile;coverage;validateJVM;coverageReport;coverageAggregate;coverageOff")
