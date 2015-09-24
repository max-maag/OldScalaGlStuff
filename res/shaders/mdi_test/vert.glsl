#version 150 core

in vec3 pos;
in mat4 model;

void main() {
	gl_Position = model * vec4(pos, 1.f);
}