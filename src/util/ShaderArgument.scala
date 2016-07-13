package util

/**
 * size is how many locations the attribute uses.
 * E.g. for matrices this is the number of columns
 */
class ShaderArgument(val location: Int, val size: Int = 1)