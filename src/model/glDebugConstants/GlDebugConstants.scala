package model.glDebugConstants

import org.lwjgl.opengl.GL11.GL_DONT_CARE
import org.lwjgl.opengl.GL43._
import model.GlConstantWrapper

trait HumanReadableConstant extends GlConstantWrapper {
  val readableName: String
  val readableValue: String
  override def toString = s"[$readableName: $readableValue]"
}

trait Source extends HumanReadableConstant {
  val readableName = "Source"
}

case object GlDebugSourceApi extends Source {
  val oglConstant = GL_DEBUG_SOURCE_API
  val readableValue = "API"
}

case object GlDebugSourceWindowSystem extends Source {
  val oglConstant = GL_DEBUG_SOURCE_WINDOW_SYSTEM
  val readableValue = "Window System"
}

case object GlDebugSourceShaderCompiler extends Source {
  val oglConstant = GL_DEBUG_SOURCE_SHADER_COMPILER
  val readableValue = "Shader Compiler"
}

case object GlDebugSourceThirdParty extends Source {
  val oglConstant = GL_DEBUG_SOURCE_THIRD_PARTY
  val readableValue = "Third Party"
}

case object GlDebugSourceApplication extends Source {
  val oglConstant = GL_DEBUG_SOURCE_APPLICATION
  val readableValue = "Application"
}

case object GlDebugSourceOther extends Source {
  val oglConstant = GL_DEBUG_SOURCE_OTHER
  val readableValue = "other"
}


trait Type extends HumanReadableConstant {
  val readableName = "Type"
}

case object GlDebugTypeError extends Type {
  val oglConstant = GL_DEBUG_TYPE_ERROR
  val readableValue = "Error"
}

case object GlDebugTypeDeprecatedBehavior extends Type {
  val oglConstant = GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR
  val readableValue = "Deprecated behaviour"
}

case object GlDebugTypeUndefinedBehavior extends Type {
  val oglConstant = GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR
  val readableValue = "Undefined Behaviour"
}

case object GlDebugTypePortability extends Type {
  val oglConstant = GL_DEBUG_TYPE_PORTABILITY
  val readableValue = "Portability"
}

case object GlDebugTypePerformance extends Type {
  val oglConstant = GL_DEBUG_TYPE_PERFORMANCE
  val readableValue = "Performance"
}

case object GlDebugTypeMarker extends Type {
  val oglConstant = GL_DEBUG_TYPE_MARKER
  val readableValue = "Marker"
}

case object GlDebugTypePushGroup extends Type {
  val oglConstant = GL_DEBUG_TYPE_PUSH_GROUP
  val readableValue = "Push Group"
}

case object GlDebugTypePopGroup extends Type {
  val oglConstant = GL_DEBUG_TYPE_POP_GROUP
  val readableValue = "Pop Group"
}

case object GlDebugTypeOther extends Type {
  val oglConstant = GL_DEBUG_TYPE_OTHER
  val readableValue = "Other"
}


trait Severity extends HumanReadableConstant {
  val readableName = "Severity"
}

case object GlDebugSeverityLow extends Severity {
  val oglConstant = GL_DEBUG_SEVERITY_LOW
  val readableValue = "Low"
}

case object GlDebugSeverityMedium extends Severity {
  val oglConstant = GL_DEBUG_SEVERITY_MEDIUM
  val readableValue = "Medium"
}

case object GlDebugSeverityHigh extends Severity {
  val oglConstant = GL_DEBUG_SEVERITY_HIGH
  val readableValue = "High"
}

case object GlDebugSeverityNotification extends Severity {
  val oglConstant = GL_DEBUG_SEVERITY_NOTIFICATION
  val readableValue = "Notification"
}


object GlDebugConstants {
  def fromConstant(c: Int) = c match {
    case GlDebugSourceApi.oglConstant => GlDebugSourceApi
    case GlDebugSourceWindowSystem.oglConstant => GlDebugSourceWindowSystem
    case GlDebugSourceShaderCompiler.oglConstant => GlDebugSourceShaderCompiler
    case GlDebugSourceThirdParty.oglConstant => GlDebugSourceThirdParty
    case GlDebugSourceApplication.oglConstant => GlDebugSourceApplication
    case GlDebugSourceOther.oglConstant => GlDebugSourceOther
    case GlDebugSeverityLow.oglConstant => GlDebugSeverityLow
    case GlDebugSeverityMedium.oglConstant => GlDebugSeverityMedium
    case GlDebugSeverityHigh.oglConstant => GlDebugSeverityHigh
    case GlDebugSeverityNotification.oglConstant => GlDebugSeverityNotification
    case GlDebugTypeError.oglConstant => GlDebugTypeError
    case GlDebugTypeDeprecatedBehavior.oglConstant => GlDebugTypeDeprecatedBehavior
    case GlDebugTypeUndefinedBehavior.oglConstant => GlDebugTypeUndefinedBehavior
    case GlDebugTypePortability.oglConstant => GlDebugTypePortability
    case GlDebugTypePerformance.oglConstant => GlDebugTypePerformance
    case GlDebugTypeMarker.oglConstant => GlDebugTypeMarker
    case GlDebugTypePushGroup.oglConstant => GlDebugTypePushGroup
    case GlDebugTypePopGroup.oglConstant => GlDebugTypePopGroup
    case GlDebugTypeOther.oglConstant => GlDebugTypeOther
  }
}

case object GlDontCare extends Source with Type with Severity {
  val oglConstant = GL_DONT_CARE
  override val readableName = "This makes no sense."
  override val readableValue = "Don't care."
}