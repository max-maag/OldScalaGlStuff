package gui.scenes

import gui.Scene

class TestScene extends Scene {
  def start(): SceneStatus = {
    SceneSuccess
  }
  
  def update(dt: Float): SceneResult = {
    SceneSuccess
  }
  
  def end() = {
    SceneSuccess
  }
}