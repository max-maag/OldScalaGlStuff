package gui

import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL43._
import org.lwjgl.opengl.GLDebugMessageCallbackI
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL

import gui.Constants.SCREEN_SIZE
import gui.scenes.DebugCallbackTestScene
import gui.scenes.ErrorScene
import gui.scenes.PersistentBufferScene
import gui.scenes.RenderManagerScene
import gui.scenes.RenderManagerScene
import gui.scenes.SceneError
import gui.scenes.SceneQuit
import gui.scenes.SceneResult
import gui.scenes.SceneSuccess
import gui.scenes.SceneTransition
import util.GlDebugConstants
import util.FpsCounter
import util.GlInfo
import util.GlUtils
import gui.scenes.MdiScene
import gui.scenes.JuliaScene
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback

object GameGUI {
  val DEFAULT_WIDTH = SCREEN_SIZE.width / 2
  val DEFAULT_HEIGHT = SCREEN_SIZE.height / 2
  val fpsCounter = new FpsCounter
  
  var window = NULL
  var scene: Scene = null //new MdiScene
  
  def main(args: Array[String]): Unit = {
    try {
      init()
      loop()
    } finally {
      glfwDestroyWindow(window)
      glfwSetErrorCallback(null).free()
    }
  }
  
  
  /** Setup OpenGL and create window. */
  def init(): Unit = {
    GLFWErrorCallback.createPrint(System.err).set()
    if(!glfwInit())
      onError("Unable to initialize GLFW")
    
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE)
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE)
    
    val width = DEFAULT_WIDTH
    val height = DEFAULT_HEIGHT
    
    window = glfwCreateWindow(width, height, "Scala OpenGL Stuff", NULL, NULL)
    if(window == NULL)
      onError("Failed to create GLFW window")
      
    glfwSetKeyCallback(window, new GLFWKeyCallback {
      override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
          glfwSetWindowShouldClose(window, true);
      }
    })
      
    glfwSetWindowPos(window, (SCREEN_SIZE.width - width)/2, (SCREEN_SIZE.height - height)/2)
    glfwMakeContextCurrent(window)
    
    glfwSwapInterval(1)
    glfwShowWindow(window)
    
    glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback {
      override def invoke(window: Long, width: Int, height: Int) = {
        glViewport(0,0,width,height)
        scene.changeResolution(width, height)
      }
    })
    
    GL.createCapabilities()
    println(s"Using OpenGL ${GlInfo.majorVersion}.${GlInfo.minorVersion}")
    
    if(GlInfo.majorVersion >= 4 && GlInfo.minorVersion >= 3 || GlInfo.supportsExtension("ARB_debug_output")) {
      glDebugMessageCallback(new GLDebugMessageCallbackI() {
        def invoke(source: Int, typ: Int, id: Int, severity: Int, length: Int, message: Long, userParam: Long) =
          println(s"GL Debug Message: ${GlDebugConstants.fromConstant(source)}${GlDebugConstants.fromConstant(typ)}"+
                  s"${GlDebugConstants.fromConstant(severity)}[ID: $id] ${MemoryUtil.memUTF8(MemoryUtil.memByteBuffer(message, length))}")
      }, 0l)
      
      GlUtils.printIfError()
      
      glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, Array.emptyIntArray, true)
      
      GlUtils.printIfError()
      
      scene = new JuliaScene(width, height)
    }
  }
  
  def loop(): Unit = {
    GL.createCapabilities()
    glClearColor(0f, 0f, 0f, 1f)
    
    var lastFrame = System.currentTimeMillis()
//    fpsCounter.start()
    checkSceneResult(scene.start())
    while(!glfwWindowShouldClose(window)) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      val dtm = System.currentTimeMillis() - lastFrame
      val dt = dtm/1000f
      
      checkSceneResult(scene.update(dt))
      
      glfwSwapBuffers(window)
      glfwPollEvents()
      lastFrame += dtm
      fpsCounter.update()
    }
    fpsCounter.stop = true
    checkSceneResult(scene.end())
  }
  
  def checkSceneResult(res: SceneResult): Unit = res match {
    case SceneError(e) => onError(e)
    case SceneQuit => glfwSetWindowShouldClose(window, true)
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