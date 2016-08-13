package gui.scenes

import gui.Scene
import org.lwjgl.opengl.GL43._
import model.Implicits._
import util.GlUtils
import org.lwjgl.opengl.GL11._
import util.GlDebugTypeOther
import util.GlDebugSourceApplication
import util.GlDebugSeverityNotification

class DebugCallbackTestScene extends Scene {
  def start() = {
    glDebugMessageInsert(GlDebugSourceApplication, GlDebugTypeOther, 0, GlDebugSeverityNotification, "This is a test message.")
    glDrawArrays(165769843, 42, 13)
    GlUtils.printIfError()
    println("start done")
    SceneSuccess
  }
  
  def update(dt: Float) = SceneSuccess
  def end() = SceneSuccess
}