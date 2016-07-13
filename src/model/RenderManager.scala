package model

import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL32._
import org.lwjgl.opengl.GL40._
import org.lwjgl.opengl.GL43._
import model.Implicits._

class RenderManager(indirectBufferSize: Long, uniformBufferSize: Long) {
  val indirectBuffer = new GlPersistentBuffer(indirectBufferSize)
  val uniformBuffer = new GlPersistentBuffer(uniformBufferSize)
  var syncObj = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
  
  /**
   * Renders the GameObjects with glMultiDrawElementsIndirect.
   * 
   * Prerequisites:
   * 	1. The VAO has the correct vertex attribute pointers are set and is bound.
   * 		 Don't forget to set the vertex attribute divisor for the uniform data.
   * 	2. All required models are loaded with the modelManager.
   * 
   * TODO: Use multiple sync objects and store which one reserved which memory region.
   * 			 This could enable rendering multiple frames simultaniously.
   */
  def render[ModelIdType](objects: Traversable[GameObject[ModelIdType]], modelManager: ModelManager[ModelIdType], mode: RenderMode) = {
    indirectBuffer.buffer.clear()
    uniformBuffer.buffer.clear()
    
    glClientWaitSync(syncObj, GL_SYNC_FLUSH_COMMANDS_BIT, 3000000)
    
    val objectsByModel = objects.groupBy(_.modelId)
    for((modelId, objs) <- objectsByModel) {
      val modelData = modelManager.getModelData(modelId)
      indirectBuffer.buffer.put(modelData.vertexCount)
      indirectBuffer.buffer.put(objs.size)
      indirectBuffer.buffer.put(modelData.indexIndex)
      indirectBuffer.buffer.put(modelData.vertexIndex)
      indirectBuffer.buffer.put(uniformBuffer.buffer.position)
        
      for(obj <- objs)
        uniformBuffer.buffer.put(obj.uniformData)
    }
    
    glMultiDrawElementsIndirect(mode.oglConstant, UnsignedInt, 0, objectsByModel.size, 0)
    
    syncObj = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
  }
  
  def bindBuffers() = {
    glBindBuffer(GL_ARRAY_BUFFER, uniformBuffer.id)
    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectBuffer.id)
  }
  
  def cleanUp() = {
    indirectBuffer.cleanUp()
    uniformBuffer.cleanUp()
  }
}