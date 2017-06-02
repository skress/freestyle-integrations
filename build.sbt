lazy val fCoreDeps =
  Seq(%%("freestyle", Option(sys.props("frees.version")).getOrElse("0.2.0")))

lazy val root = (project in file("."))
  .settings(moduleName := "root")
  .settings(name := "freestyle-integrations")
  .settings(noPublishSettings: _*)
  .aggregate(freestyleModules: _*)

lazy val monix = (crossProject in file("freestyle-monix"))
  .settings(name := "freestyle-monix")
  .jsSettings(sharedJsSettings: _*)
  .crossDepSettings(
    commonDeps ++ fCoreDeps ++
    Seq(%("monix-eval"), %("monix-cats")): _*)

lazy val monixJVM = monix.jvm
lazy val monixJS  = monix.js

lazy val cacheRedis = (project in file("freestyle-cache-redis"))
  .settings(
    name := "freestyle-cache-redis",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      %%("rediscala"),
      %%("freestyle-cache"),
      %%("akka-actor")    % "test",
      %("embedded-redis") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val doobie = (project in file("freestyle-doobie"))
  .settings(name := "freestyle-doobie")
  .settings(
    libraryDependencies ++= Seq(
      %%("doobie-core-cats"),
      %%("doobie-h2-cats") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val slick = (project in file("freestyle-slick"))
  .settings(name := "freestyle-slick")
  .settings(
    libraryDependencies ++= Seq(
      %%("slick"),
      %%("freestyle-async"),
      %("h2") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val twitterUtil = (project in file("freestyle-twitter-util"))
  .settings(name := "freestyle-twitter-util")
  .settings(
    libraryDependencies ++= Seq(%%("catbird-util")) ++ commonDeps ++ fCoreDeps
  )

lazy val fetch = (crossProject in file("freestyle-fetch"))
  .settings(name := "freestyle-fetch")
  .jsSettings(sharedJsSettings: _*)
  .crossDepSettings(
    commonDeps ++ fCoreDeps ++ Seq(
      %("fetch"),
      %("fetch-monix")
    ): _*
  )

lazy val fetchJVM = fetch.jvm
lazy val fetchJS  = fetch.js

lazy val fs2 = (crossProject in file("freestyle-fs2"))
  .settings(name := "freestyle-fs2")
  .jsSettings(sharedJsSettings: _*)
  .crossDepSettings(
    commonDeps ++ fCoreDeps ++ Seq(
      %("fs2-core")
    ): _*
  )

lazy val fs2JVM = fs2.jvm
lazy val fs2JS  = fs2.js

lazy val httpHttp4s = (project in file("http/http4s"))
  .settings(name := "freestyle-http-http4s")
  .settings(
    libraryDependencies ++= Seq(
      %%("http4s-core"),
      %%("http4s-dsl") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val httpFinch = (project in file("http/finch"))
  .settings(name := "freestyle-http-finch")
  .settings(
    libraryDependencies ++= Seq(%%("finch-core")) ++ commonDeps ++ fCoreDeps
  )

lazy val httpAkka = (project in file("http/akka"))
  .settings(name := "freestyle-http-akka")
  .settings(
    libraryDependencies ++= Seq(
      %%("akka-http"),
      %%("akka-http-testkit") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

lazy val httpPlay = (project in file("http/play"))
  .disablePlugins(CoursierPlugin)
  .settings(name := "freestyle-http-play")
  .settings(
    concurrentRestrictions in Global := Seq(Tags.limitAll(1)),
    libraryDependencies ++= Seq(
      %%("play", "2.6.0-M5")      % "test",
      %%("play-test", "2.6.0-M5") % "test"
    ) ++ commonDeps ++ fCoreDeps
  )

pgpPassphrase := Some(getEnvVar("PGP_PASSPHRASE").getOrElse("").toCharArray)
pgpPublicRing := file(s"$gpgFolder/pubring.gpg")
pgpSecretRing := file(s"$gpgFolder/secring.gpg")

lazy val freestyleModules: Seq[ProjectReference] = Seq(
  monixJVM,
  monixJS,
  cacheRedis,
  doobie,
  slick,
  twitterUtil,
  fetchJVM,
  fetchJS,
  fs2JVM,
  fs2JS,
  httpHttp4s,
  httpFinch,
  httpAkka,
  httpPlay
)
