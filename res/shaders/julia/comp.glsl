#version 430
layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;
layout(r32ui, binding = 0) uniform uimage2D img; // contains number of iterations in one 32bit uint
uniform uint maxIterations;
uniform vec2 minVal;
uniform vec2 maxVal;

void main() {
	uvec2 size = gl_NumWorkGroups.xy;
	uvec2 pos = gl_GlobalInvocationID.xy;
	
	uint itCount = 1;
	vec2 val = minVal + vec2(pos)/vec2(size) * (maxVal - minVal);
	
	while(length(val) <= 2 && itCount <= maxIterations) {
		val = vec2(val.x*val.x - val.y*val.y, 2*val.x*val.y) - vec2(0.221f, 0.713f);
		itCount++;
	}
	
	imageStore(img, ivec2(pos), uvec4(itCount));
}