#version 150 core

uniform usampler2D tex; // contains number of iterations in one 32bit uint
uniform uint maxIterations;

in vec2 uv;
out vec4 col;

void main() {
	col = vec4(vec3(float(texture(tex, uv))/maxIterations),1);
}