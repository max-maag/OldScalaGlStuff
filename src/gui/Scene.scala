package gui

import gui.scenes.SceneResult
import gui.scenes.SceneStatus

trait Scene {
  def start(): SceneStatus
  def update(dt: Float): SceneResult
  def end(): SceneStatus
  def changeResolution(width: Int, height: Int) = {}
}