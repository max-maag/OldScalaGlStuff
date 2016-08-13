#version 150 core

in vec2 pos;
out vec2 uv;

void main() {
	gl_Position = vec4(pos, 0.f, 1.f);
	uv = (pos + 1) / 2;
}