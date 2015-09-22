package gui

import java.awt.Toolkit

object Constants {
  val SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize()
  val SCREEN_ASPECT = SCREEN_SIZE.width / SCREEN_SIZE.height;
  val ASPECT = 16f / 9f
}