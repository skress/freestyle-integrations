import freestyle.FreestylePlugin
import sbt._
import sbt.Keys._

object ProjectPlugin extends AutoPlugin {

  override def requires: Plugins = FreestylePlugin

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    publishArtifact in (Compile, packageDoc) := false
  )

}
