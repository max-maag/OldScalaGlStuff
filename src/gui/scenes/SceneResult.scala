package gui.scenes

import gui.Scene

sealed trait SceneResult
case class SceneTransition(next: Scene) extends SceneResult
case object SceneQuit extends SceneResult

sealed trait SceneStatus extends SceneResult
case object SceneSuccess extends SceneStatus
case class SceneError(t: Throwable) extends SceneStatus