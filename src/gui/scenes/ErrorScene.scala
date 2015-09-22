package gui.scenes

import gui.scenes.SceneSuccess
import gui.Scene
import gui.scenes.SceneSuccess

class ErrorScene(val e: Throwable) extends Scene {
  def start() = {
    e.printStackTrace()
    SceneSuccess
  }
  def update(dt: Float) = SceneSuccess
  def end() = SceneSuccess
}