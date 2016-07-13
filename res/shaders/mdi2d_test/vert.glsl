#version 150 core

in vec2 pos;
in mat3 model;

void main() {;
	gl_Position = vec4(model * vec3(pos, 0.f), 1.f);
}