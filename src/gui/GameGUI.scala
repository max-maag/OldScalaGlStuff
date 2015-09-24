package gui

import org.lwjgl.glfw.Callbacks.errorCallbackPrint
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GLContext
import org.lwjgl.system.MemoryUtil.NULL
import gui.Constants._
import gui.scenes.SceneQuit
import gui.scenes.ErrorScene
import gui.scenes.SceneTransition
import gui.scenes.SceneSuccess
import gui.scenes.SceneResult
import gui.scenes.SceneError
import gui.scenes.QuadScene
import gui.scenes.TestScene
import gui.scenes.MdiScene

object GameGUI {
  val DEFAULT_WIDTH = SCREEN_SIZE.width / 2
  val DEFAULT_HEIGHT = SCREEN_SIZE.height / 2
  
  var window = NULL
  var scene: Scene = new MdiScene()
  
  def main(args: Array[String]): Unit = {
    try {
      init()
      loop()
    } finally {
      glfwDestroyWindow(window)
    }
  }
  
  
  /** Setup OpenGL and create window. */
  def init(): Unit = {
    glfwSetErrorCallback(errorCallbackPrint(System.err))
    if(glfwInit() != GL_TRUE)
      onError("Unable to initialize GLFW")
    
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE)
    
    val width = DEFAULT_WIDTH
    val height = DEFAULT_HEIGHT
    
    window = glfwCreateWindow(width, height, "Scala Pong", NULL, NULL)
    if(window == NULL)
      onError("Failed to create GLFW window")
      
    glfwSetKeyCallback(window, new GLFWKeyCallback {
      override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
          glfwSetWindowShouldClose(window, GL_TRUE);
      }
    })
      
    glfwSetWindowPos(window, (SCREEN_SIZE.width - width)/2, (SCREEN_SIZE.height - height)/2)
    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwShowWindow(window)
  }
  
  def loop(): Unit = {
    GLContext.createFromCurrent()
    glClearColor(0f, 0f, 0f, 1f)
    
    var lastFrame = System.currentTimeMillis()
    checkSceneResult(scene.start())
    while(glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      val dtm = System.currentTimeMillis() - lastFrame
      val dt = dtm/1000f
      
      checkSceneResult(scene.update(dt))
      
      glfwSwapBuffers(window)
      glfwPollEvents()
      lastFrame += dtm
    }
    
    checkSceneResult(scene.end())
  }
  
  def checkSceneResult(res: SceneResult): Unit = res match {
    case SceneError(e) => onError(e)
    case SceneQuit => glfwSetWindowShouldClose(window, GL_TRUE)
    case SceneTransition(s) => {
      checkSceneResult(scene.end())
      scene = s
      checkSceneResult(scene.start())
    }
    case SceneSuccess =>
  }
  
  def onError(e: Throwable): Unit = {
    if(window == NULL) {
      e.printStackTrace()
      System.exit(1)
    }
    
    scene = new ErrorScene(e)
    scene.start()
  }
  
  def onError(err: String): Unit = {
    if(window == NULL) {
      System.err.println(err)
      System.exit(1)
    }
    
    scene = new ErrorScene(new RuntimeException("Error @ unkown: " + err))
    scene.start()
  }
}