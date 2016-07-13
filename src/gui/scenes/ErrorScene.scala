package gui.scenes

import gui.Scene

class ErrorScene(val e: Throwable) extends Scene {
  def start() = {
    e.printStackTrace()
    SceneSuccess
  }
  def update(dt: Float) = SceneSuccess
  def end() = SceneSuccess
}